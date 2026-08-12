package com.demo.bookstorebatch.ftp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class FtpConfig {
    //@Autowired(required = false)
    private final SessionFactory<FTPFile> ftpSessionFactory;
    private final JobOperator jobOperator;
    private final Job importJob;

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory() {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost("://localhost");
        factory.setPort(21);
        factory.setUsername("bookstore");
        factory.setPassword("bookstore123");
        return factory;
    }

    @Bean
    IntegrationFlow ftpInboundFlow() {
        return IntegrationFlow
                .from(
                        Ftp.inboundAdapter(ftpSessionFactory)
                                .remoteDirectory("/incoming")
                                .localDirectory(new File("./ftp/incoming"))
                                .autoCreateLocalDirectory(true)
                                .deleteRemoteFiles(true),

                        e -> e.poller(
                                Pollers.fixedDelay(5000)
                        )
                )

                .handle(File.class, (file, headers) -> {
                    String entity = extractEntity(file);

                    JobParameters parameters =
                            new JobParametersBuilder()
                                    .addString("file",
                                            file.getAbsolutePath()
                                    )
                                    .addString("entity",
                                            entity
                                    )
                                    .addLong("timestamp",
                                            System.currentTimeMillis()
                                    )
                                    .toJobParameters();

                    try {
                        jobOperator.start(importJob, parameters);
                    } catch (Exception e) {
                        throw new IllegalStateException("Could not launch batch job", e);
                    }

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
