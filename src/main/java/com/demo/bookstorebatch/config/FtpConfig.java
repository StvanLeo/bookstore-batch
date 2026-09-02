package com.demo.bookstorebatch.ftp;

import com.demo.bookstorebatch.service.BatchLauncher;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class FtpConfig {
    private final BatchLauncher batchLauncher;

    @Bean
    public DefaultFtpSessionFactory ftpSessionFactory(
            @Value("${ftp.host}") String host,
            @Value("${ftp.port}") int port,
            @Value("${ftp.username}") String username,
            @Value("${ftp.password}") String password
    ) {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        // Use passive FTP
        factory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);

        // Binary transfer
        factory.setFileType(FTPClient.BINARY_FILE_TYPE);

        return factory;
    }

    @Bean
    public IntegrationFlow ftpInboundFlow(
            DefaultFtpSessionFactory ftpSessionFactory,
            @Value("${ftp.remote-directory}") String remoteDirectory,
            @Value("${batch.local-directory}") String localDirectory
    ) {
        return IntegrationFlow
                .from(
                        Ftp.inboundAdapter(ftpSessionFactory)
                                .preserveTimestamp(true)
                                .remoteDirectory(remoteDirectory)
                                .localDirectory(new File(localDirectory))
                                .deleteRemoteFiles(false)
                                .autoCreateLocalDirectory(true)
                                .temporaryFileSuffix(".writing"),
                        e -> e.poller(
                                Pollers.fixedDelay(5000)
                        )
                )
                .handle(File.class, (file, headers) -> {
                    batchLauncher.launch(file);

                    return null;
                })
                .get();
    }

    private String extractEntity(File file) {

        String filename = file.getName();

        if (filename.equalsIgnoreCase("pub.csv")) {
            return "publisher";
        }

        if (filename.equalsIgnoreCase("authors.csv")) {
            return "author";
        }

        if (filename.equalsIgnoreCase("books.csv")) {
            return "book";
        }

        throw new IllegalArgumentException(
                "Unknown CSV file: " + filename
        );
    }
}
