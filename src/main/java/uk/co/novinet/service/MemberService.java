package uk.co.novinet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.co.novinet.rest.PaymentType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.co.novinet.service.ContributionType.DONATION;
import static uk.co.novinet.service.PersistenceUtils.*;

@Service
public class MemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);
    private static final String LCAG_FFC_CONTRIBUTOR_GROUP = "";
    private static final String LCAG_FFC_CONTRIBUTOR_ENHANCED_SUPPORT_GROUP = "9";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${vatRate}")
    private String vatRate;

    @Value("${vatNumber}")
    private String vatNumber;

    @Value("${contributionAgreementMinimumAmountGbp}")
    private BigDecimal contributionAgreementMinimumAmountGbp;

    private static Map<String, Member> memberByUsernameCache = new ConcurrentHashMap<>();

    private static Map<String, Member> memberByEmailCache = new ConcurrentHashMap<>();

    private static Map<Long, Member> memberByIdCache = new ConcurrentHashMap<>();

    @Scheduled(initialDelayString = "${refreshMemberCacheInitialDelayMilliseconds}", fixedRateString = "${refreshMemberCacheIntervalMilliseconds}")
    public void refreshMemberCache() {
        List<Member> members = jdbcTemplate.query(buildUserTableSelect() + buildUserTableGroupBy(), new Object[]{}, (rs, rowNum) -> buildMember(rs));

        for (Member member : members) {
            memberByUsernameCache.put(member.getUsername().toLowerCase(), member);
            memberByEmailCache.put(member.getEmailAddress().toLowerCase(), member);
            memberByIdCache.put(member.getId(), member);
        }

        List<String> usernamesInDb = members.stream().map(member -> member.getUsername().toLowerCase()).collect(Collectors.toList());
        List<String> emailAddressesInDb = members.stream().map(member -> member.getEmailAddress().toLowerCase()).collect(Collectors.toList());
        List<Long> idsInDb = members.stream().map(Member::getId).collect(Collectors.toList());

        Set<String> usernamesInCache = new HashSet<>(memberByUsernameCache.keySet());
        Set<String> emailAddressesInCache = new HashSet<>(memberByEmailCache.keySet());
        Set<Long> idsInCache = new HashSet<>(memberByIdCache.keySet());

        usernamesInCache.removeAll(usernamesInDb);
        emailAddressesInCache.removeAll(emailAddressesInDb);
        idsInCache.removeAll(idsInDb);

        for (String usernameToDelete : usernamesInCache) {
            memberByUsernameCache.remove(usernameToDelete);
        }

        for (String emailToDelete : emailAddressesInCache) {
            memberByEmailCache.remove(emailToDelete);
        }

        for (Long idToDelete : idsInCache) {
            memberByIdCache.remove(idToDelete);
        }
    }

    public Member findMemberByUsername(String username) {
        return memberByUsernameCache.get(username.toLowerCase());
    }

    public Member findMemberById(Long id) {
        return memberByIdCache.get(id);
    }

    private Member buildMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getLong("uid"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("group"),
                rs.getString("additionalgroups"),
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
        return jdbcTemplate.query(buildUserTableSelect() + " where lower(u." + field + ") = ?", new Object[] { value.toLowerCase() }, (rs, rowNum) -> buildMember(rs));
    }

    private String buildUserTableSelect() {
        return "select u.uid, u.username, u.name, u.email, u.regdate, u.hmrc_letter_checked, u.identification_checked, u.agreed_to_contribute_but_not_paid, " +
                "u.mp_name, u.mp_engaged, u.mp_sympathetic, u.mp_constituency, u.mp_party, u.schemes, u.notes, u.industry, u.token, u.has_completed_membership_form, " +
                "u.how_did_you_hear_about_lcag, u.member_of_big_group, u.big_group_username, u.verified_on, u.verified_by, u.already_have_an_lcag_account_email_sent, " +
                "u.registered_for_claim, u.has_completed_claim_participant_form, u.has_been_sent_claim_confirmation_email, u.opted_out_of_claim, u.claim_token, ug.title as `group`, u.additionalgroups " +
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
                    forumGroupForContributionType(payment),
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
                    "`mp_name`, `mp_constituency`, `mp_party`, `mp_engaged`, `mp_sympathetic`, `schemes`, `industry`, `how_did_you_hear_about_lcag`, `member_of_big_group`) " +
                    "VALUES (?, ?, ?, ?, 'lvhLksjhHGcZIWgtlwNTJNr3bjxzCE2qgZNX6SBTBPbuSLx21u', ?, 0, 0, '', '', '', 8, ?, 0, '', ?, ?, ?, 0, '', '0', '', '', '', '', '', " +
                    "'all', '', 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 'linear', 1, 1, 1, 1, 1, 1, 0, 0, 0, '', '', '', 0, 0, '', '', 0, 0, 0, '0', '', '', '', 0, 0, 0, '', '', '', 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, " +
                    "0, 0, 1, '', 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            LOGGER.info("Going to execute insert sql: {}", insertSql);

            int result = jdbcTemplate.update(insertSql,
                    nextAvailableId,
                    member.getUsername(),
                    member.getPasswordDetails().getPasswordHash(),
                    member.getPasswordDetails().getSalt(),
                    member.getEmailAddress(),
                    member.getAdditionalGroups(),
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
        if (isBlank(emailAddress)) {
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

    public void fillInPaymentBlanks(Payment payment, Member member, boolean memberAlreadyExisted) {
        if (member != null) {
            payment.setMembershipToken(member.getToken());

            if (memberAlreadyExisted) {
                payment.setUsername(member.getUsername());
                payment.setPaymentType(PaymentType.EXISTING_LCAG_MEMBER);
            }
        }

        payment.setGuid(guid());
        payment.setVatNumber(vatNumber);

        if (payment.getContributionType() == DONATION) {
            payment.setVatRate(BigDecimal.ZERO);
            payment.setNetAmount(payment.getGrossAmount());
            payment.setVatAmount(BigDecimal.ZERO);
        } else {
            BigDecimal vatRate = new BigDecimal(this.vatRate);
            payment.setVatRate(vatRate);
            payment.setNetAmount(calculateNetAmount(payment.getGrossAmount(), vatRate));
            payment.setVatAmount(payment.getGrossAmount().subtract(payment.getNetAmount()));
        }

        switch (payment.getPaymentType()) {
            case ANONYMOUS:
                return;
            case NEW_LCAG_MEMBER:
                payment.setHash(member.getPasswordDetails().getPasswordHash());
                payment.setUsername(member.getUsername());
                payment.setUserId(member.getId());
                return;
            case EXISTING_LCAG_MEMBER:
                if (payment.getContributionType() == DONATION) {
                    // if it's a donation, we won't have this stuff in the payment so get it from the member
                    payment.setUsername(member.getUsername());
                    payment.setFirstName(firstName(member.getName()));
                    payment.setLastName(lastName(member.getName()));
                    payment.setEmailAddress(member.getEmailAddress());
                }
                payment.setUserId(member.getId());
                return;
        }
    }

    BigDecimal calculateNetAmount(BigDecimal grossAmount, BigDecimal vatRate) {
        BigDecimal vatAsFraction = vatRate.divide(new BigDecimal(100).setScale(2)).setScale(2);
        return grossAmount.setScale(2).divide(new BigDecimal(1).add(vatAsFraction).setScale(2), BigDecimal.ROUND_HALF_EVEN).setScale(2);
    }

    private String lastName(String name) {
        if (isBlank(name)) {
            return "";
        }

        List<String> parts = asList(name.split("(\\s)+"));
        Collections.reverse(parts);
        return parts.get(0);
    }


    private String firstName(String name) {
        if (isBlank(name)) {
            return "";
        }

        List<String> nameParts = new ArrayList<>(asList(name.split("(\\s)+")));

        if (nameParts.size() == 1) {
            return nameParts.get(0);
        }

        nameParts.remove(nameParts.size() - 1);

        return nameParts.stream().collect(joining(" "));
    }

    public void assignLcagFfcAdditionalGroup(Member member, Payment payment) {
        LOGGER.info("Going to assign member: {} - to LCAG FFC forum group for payment: {}", member, payment);

        if (isBlank(forumGroupForContributionType(payment))) {
            LOGGER.info("No new group for member: {} and payment: {} as payment is not sufficiently high", member, payment);
            return;
        }

        String updateSql = "update " + usersTableName() + " set `additionalgroups` = ? where uid = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(
                updateSql,
                forumGroupForContributionType(payment),
                member.getId()
        );

        LOGGER.info("Update result: {}", result);
    }

    String forumGroupForContributionType(Payment payment) {
        return payment.getContributionType() == DONATION ? LCAG_FFC_CONTRIBUTOR_GROUP : LCAG_FFC_CONTRIBUTOR_ENHANCED_SUPPORT_GROUP;
    }
}
