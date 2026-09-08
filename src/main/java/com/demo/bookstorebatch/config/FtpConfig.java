package com.demo.bookstorebatch.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

import java.io.File;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class FtpConfig {
    private final FtpBatchJobLauncher batchLauncher;
    private static final Logger log = LoggerFactory.getLogger(FtpConfig.class);

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory(
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

        log.info("FTP server: {}:{}", host, port);

        return new CachingSessionFactory<>(factory);
    }

    @Bean
    public IntegrationFlow ftpInboundFlow(
            SessionFactory<FTPFile> ftpSessionFactory,
            @Value("${ftp.remote-directory}") String remoteDirectory,
            @Value("${ftp.local-directory}") String localDirectory,
            @Value("${ftp.poll-interval}") long pollInterval
    ) {
        log.info("=== Creating FTP Inbound Flow ===");
        log.info("Remote Directory: {}", remoteDirectory);
        log.info("Local Directory: {}", localDirectory);

        return IntegrationFlow
                .from(
                        Ftp.inboundAdapter(ftpSessionFactory)
                                .preserveTimestamp(true)
                                .remoteDirectory(remoteDirectory)
                                .localDirectory(new File(localDirectory))
                                .deleteRemoteFiles(true)
                                .autoCreateLocalDirectory(true)
                                .patternFilter("*.csv"),
                        endpoint -> endpoint.poller(
                                Pollers.fixedDelay(pollInterval)
                        )
                )
                .handle(File.class, (file, headers) -> {
                    log.info(file.getAbsolutePath());
                    log.info(headers.toString());
                    batchLauncher.launch(file);

                    return null;
                })
                .get();
    }
}
