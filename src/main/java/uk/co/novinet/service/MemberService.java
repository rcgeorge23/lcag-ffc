package uk.co.novinet.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.co.novinet.rest.PaymentStatus;
import uk.co.novinet.rest.PaymentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static uk.co.novinet.service.PersistenceUtils.*;

@Service
public class MemberService {
    private static final long REFERENCE_SEED = 90000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Map<String, Member> memberByUsernameCache = new ConcurrentHashMap<>();

    private Map<String, Member> memberByEmailCache = new ConcurrentHashMap<>();

    @Scheduled(initialDelayString = "${refreshMemberCacheInitialDelayMilliseconds}", fixedRateString = "${refreshMemberCacheIntervalMilliseconds}")
    public void refreshMemberCache() {
        List<Member> members = jdbcTemplate.query(buildUserTableSelect() + buildUserTableGroupBy(), new Object[]{}, (rs, rowNum) -> buildMember(rs));
        for (Member member : members) {
            memberByUsernameCache.put(member.getUsername().toLowerCase(), member);
            memberByEmailCache.put(member.getEmailAddress().toLowerCase(), member);
        }
    }

    public Member findMemberByUsername(String username) {
        return memberByUsernameCache.get(username.toLowerCase());
    }

    private Member buildMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getLong("uid"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("group"),
                dateFromMyBbRow(rs, "regdate"),
                rs.getBoolean("hmrc_letter_checked"),
                rs.getBoolean("identification_checked"),
                rs.getString("mp_name"),
                rs.getString("schemes"),
                rs.getBoolean("mp_engaged"),
                rs.getBoolean("mp_sympathetic"),
                rs.getString("mp_constituency"),
                rs.getString("mp_party"),
                rs.getBoolean("agreed_to_contribute_but_not_paid"),
                rs.getString("notes"),
                rs.getString("industry"),
                rs.getString("token"),
                rs.getBoolean("has_completed_membership_form"),
                null,
                rs.getString("how_did_you_hear_about_lcag"),
                rs.getBoolean("member_of_big_group"),
                rs.getString("big_group_username"),
                rs.getString("verified_by"),
                dateFromMyBbRow(rs, "verified_on"),
                rs.getBoolean("already_have_an_lcag_account_email_sent"),
                rs.getBoolean("registered_for_claim"),
                rs.getBoolean("has_completed_claim_participant_form"),
                rs.getBoolean("has_been_sent_claim_confirmation_email"),
                rs.getBoolean("opted_out_of_claim"),
                rs.getString("claim_token")
        );
    }

    public List<Member> findExistingForumUsersByField(String field, String value) {
        return jdbcTemplate.query(buildUserTableSelect() + "where lower(u." + field + ") = ?" + buildUserTableGroupBy(), new Object[] { value.toLowerCase() }, (rs, rowNum) -> buildMember(rs));
    }

    private String buildUserTableSelect() {
        return "select u.uid, u.username, u.name, u.email, u.regdate, u.hmrc_letter_checked, u.identification_checked, u.agreed_to_contribute_but_not_paid, " +
                "u.mp_name, u.mp_engaged, u.mp_sympathetic, u.mp_constituency, u.mp_party, u.schemes, u.notes, u.industry, u.token, u.has_completed_membership_form, " +
                "u.how_did_you_hear_about_lcag, u.member_of_big_group, u.big_group_username, u.verified_on, u.verified_by, u.already_have_an_lcag_account_email_sent, " +
                "u.registered_for_claim, u.has_completed_claim_participant_form, u.has_been_sent_claim_confirmation_email, u.opted_out_of_claim, u.claim_token, ug.title as `group` " +
                "from " + usersTableName() + " u inner join " + userGroupsTableName() + " ug on u.usergroup = ug.gid";
    }

    private String buildUserTableGroupBy() {
        return " group by u.uid ";
    }

    public MemberCreationResult createForumUserIfNecessary(Payment payment) {
        if (payment.getPaymentType() == PaymentType.ANONYMOUS) {
            return null;
        }

        Member member = memberByEmailCache.get(payment.getEmailAddress().toLowerCase());

        if (member != null) {
            LOGGER.info("Already existing forum user with email address {}", payment.getEmailAddress());
            LOGGER.info("Skipping");
            return new MemberCreationResult(true, member);
        } else {
            LOGGER.info("No existing forum user found with email address: {}", payment.getEmailAddress());
            LOGGER.info("Going to create one");

            member = new Member(
                    null,
                    payment.getEmailAddress(),
                    extractUsername(payment.getEmailAddress()),
                    payment.getFirstName() + " " + payment.getLastName(),
                    null,
                    Instant.now(),
                    false,
                    false,
                    "",
                    "",
                    false,
                    false,
                    "",
                    "",
                    false,
                    "",
                    "",
                    guid(),
                    false,
                    PasswordSource.getRandomPasswordDetails(),
                    "",
                    false,
                    "",
                    "",
                    null,
                    false,
                    false,
                    false,
                    false,
                    false,
                    guid()
            );

            Long nextAvailableId = findNextAvailableId("uid", usersTableName());

            String insertSql = "insert into " + usersTableName() + " (`uid`, `username`, `password`, `salt`, `loginkey`, `email`, `postnum`, `threadnum`, `avatar`, " +
                    "`avatardimensions`, `avatartype`, `usergroup`, `additionalgroups`, `displaygroup`, `usertitle`, `regdate`, `lastactive`, `lastvisit`, `lastpost`, `website`, `icq`, " +
                    "`aim`, `yahoo`, `skype`, `google`, `birthday`, `birthdayprivacy`, `signature`, `allownotices`, `hideemail`, `subscriptionmethod`, `invisible`, `receivepms`, `receivefrombuddy`, " +
                    "`pmnotice`, `pmnotify`, `buddyrequestspm`, `buddyrequestsauto`, `threadmode`, `showimages`, `showvideos`, `showsigs`, `showavatars`, `showquickreply`, `showredirect`, `ppp`, `tpp`, " +
                    "`daysprune`, `dateformat`, `timeformat`, `timezone`, `dst`, `dstcorrection`, `buddylist`, `ignorelist`, `style`, `away`, `awaydate`, `returndate`, `awayreason`, `pmfolders`, `notepad`, " +
                    "`referrer`, `referrals`, `reputation`, `regip`, `lastip`, `language`, `timeonline`, `showcodebuttons`, `totalpms`, `unreadpms`, `warningpoints`, `moderateposts`, `moderationtime`, " +
                    "`suspendposting`, `suspensiontime`, `suspendsignature`, `suspendsigtime`, `coppauser`, `classicpostbit`, `loginattempts`, `usernotes`, `sourceeditor`, `name`, `token`, `has_completed_membership_form`, `claim_token`, " +
                    "`mp_name`, `mp_constituency`, `mp_party`, `mp_engaged`, `mp_sympathetic`, `schemes`, `industry`, `how_did_you_hear_about_lcag`, `member_of_big_group`, `big_group_username`) " +
                    "VALUES (?, ?, ?, ?, 'lvhLksjhHGcZIWgtlwNTJNr3bjxzCE2qgZNX6SBTBPbuSLx21u', ?, 0, 0, '', '', '', 8, '', 0, '', ?, ?, ?, 0, '', '0', '', '', '', '', '', " +
                    "'all', '', 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 'linear', 1, 1, 1, 1, 1, 1, 0, 0, 0, '', '', '', 0, 0, '', '', 0, 0, 0, '0', '', '', '', 0, 0, 0, '', '', '', 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, " +
                    "0, 0, 1, '', 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            LOGGER.info("Going to execute insert sql: {}", insertSql);

            int result = jdbcTemplate.update(insertSql,
                    nextAvailableId,
                    member.getUsername(),
                    member.getPasswordDetails().getPasswordHash(),
                    member.getPasswordDetails().getSalt(),
                    member.getEmailAddress(),
                    unixTime(member.getRegistrationDate()),
                    0L,
                    0L,
                    member.getName(),
                    member.getToken(),
                    false,
                    member.getClaimToken(),
                    "",
                    "",
                    "",
                    false,
                    false,
                    "",
                    "",
                    "",
                    false,
                    false
            );

            member.setId(nextAvailableId);

            LOGGER.info("Insertion result: {}", result);

            return new MemberCreationResult(false, member);
        }
    }

    private String emptyStringIfNull(String string) {
        return string == null ? "" : string;
    }

    private String guid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String extractUsername(String emailAddress) {
        if (StringUtils.isBlank(emailAddress)) {
            return "";
        }

        String usernameCandidate = firstBitOfEmailAddress(emailAddress);
        LOGGER.info("Candidate username: {}", usernameCandidate);

        if (usernameCandidate.length() < 3 || !findExistingForumUsersByField("username", usernameCandidate).isEmpty()) {
            do {
                LOGGER.info("Candidate username: {} already exists! Going to try creating another one.", usernameCandidate);
                usernameCandidate = usernameCandidate(emailAddress);
                LOGGER.info("New candidate username: {}", usernameCandidate);
            } while (!findExistingForumUsersByField("username", usernameCandidate).isEmpty());
        }

        LOGGER.info("Settled on username: {}", usernameCandidate);

        return usernameCandidate;
    }

    private String usernameCandidate(String emailAddress) {
        return firstBitOfEmailAddress(emailAddress) + randomDigit() + randomDigit();
    }

    private String randomDigit() {
        return String.valueOf(new Random().nextInt(9));
    }

    private String firstBitOfEmailAddress(String emailAddress) {
        return emailAddress.substring(0, emailAddress.indexOf("@"));
    }

    public void fillInBlanks(Payment payment, MemberCreationResult memberCreationResult) {
        if (memberCreationResult != null) {
            payment.setMembershipToken(memberCreationResult.getMember().getToken());

            if (memberCreationResult.memberAlreadyExisted()) {
                payment.setPaymentType(PaymentType.EXISTING_LCAG_MEMBER);
            }
        }

        switch (payment.getPaymentType()) {
            case ANONYMOUS:
                return;
            case NEW_LCAG_MEMBER:
                payment.setHash(memberCreationResult.getMember().getPasswordDetails().getPasswordHash());
                return;
            case EXISTING_LCAG_MEMBER:
                Member member = memberByUsernameCache.get(payment.getUsername());
                payment.setFirstName(firstName(member.getName()));
                payment.setLastName(lastName(member.getName()));
                payment.setEmailAddress(member.getEmailAddress());
                payment.setUserId(member.getId());
                return;
        }
    }

    private String lastName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        List<String> parts = asList(name.split("(\\s)+"));
        Collections.reverse(parts);
        return parts.get(0);
    }


    private String firstName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        List<String> nameParts = new ArrayList<>(asList(name.split("(\\s)+")));

        if (nameParts.size() == 1) {
            return nameParts.get(0);
        }

        nameParts.remove(nameParts.size() - 1);

        return nameParts.stream().collect(joining(" "));
    }

    public Payment createFfcContribution(Payment payment) {
        LOGGER.info("Going to create new contribution for payment: {}", payment);

        Long nextAvailableId = findNextAvailableId("id", contributionsTableName());

        String insertSql = "insert into " + contributionsTableName() + " (`id`, `user_id`, `username`, `hash`, `membership_token`, `first_name`, `last_name`, `email_address`, `amount`, `date`, `type`, `stripe_token`, `status`, `reference`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        LOGGER.info("Going to execute insert sql: {}", insertSql);

        int result = jdbcTemplate.update(insertSql,
                nextAvailableId,
                payment.getUserId(),
                payment.getUsername(),
                payment.getHash(),
                payment.getMembershipToken(),
                payment.getFirstName(),
                payment.getLastName(),
                payment.getEmailAddress(),
                payment.getAmount(),
                unixTime(Instant.now()),
                payment.getPaymentType().toString(),
                payment.getStripeToken(),
                PaymentStatus.NEW.toString(),
                buildReference(nextAvailableId)
            );

        LOGGER.info("Insertion result: {}", result);

        payment.setId(nextAvailableId);

        return payment;
    }

    public void updateFfcContributionStatus(Payment payment, PaymentStatus paymentStatus) {
        LOGGER.info("Going to update contribution: {} payment status to : {}", payment, paymentStatus);

        String updateSql = "update " + contributionsTableName() + " set `status` = ? where id = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(updateSql, paymentStatus.toString(), payment.getId());

        LOGGER.info("Update result: {}", result);
    }

    public List<Payment> getFfcContributionsAwaitingEmails() {
        LOGGER.info("Going to find contributions awaiting emails");

        String sql = "select * from " + contributionsTableName() + " where `email_sent` = 0;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> buildPayment(rs));
    }

    private Payment buildPayment(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("username"),
                rs.getString("membership_token"),
                rs.getString("hash"),
                rs.getString("reference"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email_address"),
                rs.getBigDecimal("amount"),
                dateFromMyBbRow(rs, "date"),
                rs.getString("stripe_token"),
                PaymentStatus.valueOf(rs.getString("status")),
                PaymentType.valueOf(rs.getString("payment_type")),
                ContributionType.valueOf(rs.getString("contribution_type"))
        );
    }

    private String buildReference(Long nextAvailableId) {
        return "LCAGFFC" + (REFERENCE_SEED + nextAvailableId);
    }

    public void markContributionEmailSent(Payment payment) {
        LOGGER.info("Going to mark email sent for contribution: {}", payment);
        String updateSql = "update " + contributionsTableName() + " set `email_sent` = 1 where id = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(updateSql, payment.getId());

        LOGGER.info("Update result: {}", result);
    }
}
