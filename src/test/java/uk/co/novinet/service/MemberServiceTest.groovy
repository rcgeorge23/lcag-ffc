package uk.co.novinet.service

import spock.lang.Specification

class MemberServiceTest extends Specification {
    MemberService testObj
    def paymentMock = Mock(Payment)

    def setup() {
        testObj = new MemberService()
        testObj.minimumContributionAmountForEnhancedSupport = new BigDecimal("250")
    }

    def donationOfLessThan250ResultsInNormalLcagFfcForumGroup() {
        given:
        paymentMock.getGrossAmount() >> new BigDecimal("249.99")

        expect:
        testObj.forumGroupForContributionAmount(paymentMock) == "9"
    }

    def donationOf250ResultsInEnhancedLcagFfcForumGroup() {
        given:
        paymentMock.getGrossAmount() >> new BigDecimal("250.00")

        expect:
        testObj.forumGroupForContributionAmount(paymentMock) == "9,10"
    }

    def donationOfMoreThan250ResultsInEnhancedLcagFfcForumGroup() {
        given:
        paymentMock.getGrossAmount() >> new BigDecimal("250.01")

        expect:
        testObj.forumGroupForContributionAmount(paymentMock) == "9,10"
    }
}
