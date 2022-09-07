package org.oz.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class StsDemoIT
{
    private static final Logger log = LoggerFactory.getLogger(StsDemoIT.class);
    private static final String ROLE_ARN="arn:aws:iam::378101824585:role/sqs-dummy-role";

    @Test
    public void testRequestSessionToken(){

        StsClient stsClient = StsClient.builder().build();
        GetSessionTokenRequest sessionTokenRequest = GetSessionTokenRequest.builder()
                .durationSeconds(900).build();

        GetSessionTokenResponse sessionTokenResponse= stsClient.getSessionToken(sessionTokenRequest);
        Credentials credentials=sessionTokenResponse.credentials();

        log.info("access key:{}" ,credentials.accessKeyId());
        log.info("session token:{}" ,credentials.sessionToken());
        log.info("expiration:{}" ,credentials.expiration());
        log.info("secret access key:{}" ,credentials.secretAccessKey());
    }


    @Test
    public void testGetTemporaryCredentialsAndAssumeRole(){

        AssumeRoleRequest assumeRoleRequest=AssumeRoleRequest.builder()
                .roleArn(ROLE_ARN)
                .roleSessionName("dummy-session-name")
                .build();

        StsClient stsClient = StsClient.builder().build();
        AssumeRoleResponse roleResponse = stsClient.assumeRole(assumeRoleRequest);

        Credentials credentials = roleResponse.credentials();

        log.info("{}", credentials);

        SqsClient sqsClient= SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsSessionCredentials.create(credentials.accessKeyId(),
                                credentials.secretAccessKey(),
                                credentials.sessionToken())
                        ))
                .build();

        log.info("Listing queues");
        listQueues(sqsClient);

    }

    private void listQueues(SqsClient sqsClient){

        ListQueuesResponse listOfQueues = sqsClient.listQueues();

        for (String url : listOfQueues.queueUrls()) {
            log.info(url);
            GetQueueAttributesResponse queueAttributesResponse= sqsClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(url)
                            .attributeNames(QueueAttributeName.ALL)
                            .build()
            );

            log.info("{}", queueAttributesResponse);
        }
    }
}
