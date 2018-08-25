#!/usr/bin/env bash

echo "Standing up lcag-ffc application"
docker run -d \
    --network lcag-automation-network \
    -e "SPRING_PROFILES_ACTIVE=prod" \
	-e "SMTP_HOST=lcag-mail" \
	-e "SMTP_PORT=3025" \
	-e "SMTP_USERNAME=lcag-testing@lcag.com" \
	-e 'SMTP_PASSWORD=password' \
	-e "MYBB_FORUM_DATABASE_URL=jdbc:mysql://lcag-mysql/mybb" \
	-e "MYBB_FORUM_DATABASE_USERNAME=root" \
	-e "MYBB_FORUM_DATABASE_PASSWORD=p@ssword" \
	-e "BCC_RECIPIENTS=test@bcc.com" \
	-e "EMAIL_FROM_NAME=LCAG" \
	-e "THANK_YOU_FOR_YOUR_CONTRIBUTION_EMAIL_SOURCE_URL=https://docs.google.com/document/d/1TS2ym_lv5uszANO4P3EWhqRXf95BmVCC9iTPXVx3ayU/export?format=html" \
	-e "THANK_YOU_FOR_YOUR_CONTRIBUTION_EMAIL_SUBJECT=Thank you for your litigation contribution" \
    -e "NEW_MEMBER_EMAIL_SOURCE_URL=https://docs.google.com/document/d/1vUf4xxb6G5QhS_CPYaKvG2p7LbGx4gnqf7HKTDfASN0/export?format=html" \
	-e "NEW_MEMBER_EMAIL_SUBJECT=Loan Charge Action Group - joining instructions" \
	-e "PUBLISHABLE_STRIPE_API_KEY=xxxx" \
	-e "SECRET_STRIPE_API_KEY=xxxx" \
	-e "REFRESH_MEMBER_CACHE_INITIAL_DELAY_MILLISECONDS=1000" \
	-e "REFRESH_MEMBER_CACHE_INTERVAL_MILLISECONDS=1000" \
    -e "SEND_EMAILS_INITIAL_DELAY_MILLISECONDS=1000" \
	-e "SEND_EMAILS_INTERVAL_MILLISECONDS=1000" \
	-e "VIRTUAL_PORT=8484" \
	-e "SERVER_PORT=8484" \
	--name lcag-claim \
    -p 8484:8484 \
    -t dockernovinet/lcag-claim

echo "Waiting for application status url to respond with 200"
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8484)" != "200" ]]; do sleep 5; done