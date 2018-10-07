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
    -e "EXISTING_MEMBER_THANK_YOU_FOR_YOUR_CONTRIBUTION_AGREEMENT_EMAIL_SOURCE_URL=https://docs.google.com/document/d/1JwN1yHLJFjTi6fxU_-8oX4LBBbPgSRaQ1xdj5A0x244/export?format=html" \
    -e "EXISTING_MEMBER_THANK_YOU_FOR_YOUR_CONTRIBUTION_AGREEMENT_EMAIL_SUBJECT=Thank you for your litigation contribution" \
    -e "NEW_MEMBER_CONTRIBUTION_AGREEMENT_EMAIL_SOURCE_URL=https://docs.google.com/document/d/1YHIS5D99S-PNmmOoqgjrifUzd7iIHwwAXpzR1VCfIrA/export?format=html" \
    -e "NEW_MEMBER_CONTRIBUTION_AGREEMENT_EMAIL_SUBJECT=Loan Charge Action Group - joining instructions" \
	-e "PUBLISHABLE_STRIPE_API_KEY=$LCAG_TEST_PUBLISHED_STRIPE_KEY" \
	-e "SECRET_STRIPE_API_KEY=$LCAG_TEST_SECRET_STRIPE_KEY" \
	-e "CONTRIBUTION_AGREEMENT_MINIMUM_AMOUNT_GBP=600" \
	-e "VAT_NUMBER=<PENDING>" \
	-e "REFRESH_MEMBER_CACHE_INITIAL_DELAY_MILLISECONDS=1000" \
	-e "REFRESH_MEMBER_CACHE_INTERVAL_MILLISECONDS=1000" \
    -e "SEND_EMAILS_INITIAL_DELAY_MILLISECONDS=1000" \
	-e "SEND_EMAILS_INTERVAL_MILLISECONDS=1000" \
	-e "VAT_RATE=20" \
	-e "VIRTUAL_PORT=8484" \
	-e "SERVER_PORT=8484" \
	--name lcag-ffc \
    -p 8484:8484 \
    -t dockernovinet/lcag-ffc

echo "Waiting for application status url to respond with 200"
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8484)" != "200" ]]; do sleep 5; done