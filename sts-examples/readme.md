# sts-examples

Demo proect with examples to show how to use Secure Token Service (STS).

## Requirements

1. **Create a new user** in aws with *Programmatic access* with no permissions to get their credentials (`Secret key`, `Access key`)
2. **Create a new role** with the *Custom trust policy* assigning the user ARN and granting action `sts:AssumeRole` as trusted relationship, and grant the permission to `AmazonSQSReadOnlyAccess`.
   ```javascript
   {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::378101824585:user/dummy-user"
            },
            "Action": "sts:AssumeRole"
        }
    ]
    }
   ```
3. **Create a queue**, go to SQS and create new queue and add a dummy message
4. **Create a configuration directory** in your home called `.aws`
   ```bash
    mkdir $HOME/.aws
   ```
5. **Create the config files** named `config` and `credentials` with the next content, the region must be the same where you had created the queue.

   **config**
   ```text
    [default]
    region =us-east-2
   ```
   **credentials**
   ```text
    [default]
    aws_access_key_id = <SECRET KEY HERE>
    aws_secret_access_key = <SECRET ACCESS KEY HERE>
   ```
6. Execute the integration tests

## Build

### Test

```bash 
# Testing
mvn compile failsafe:integration-test 
```