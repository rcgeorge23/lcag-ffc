package uk.co.novinet.service

import spock.lang.Specification

class MemberServiceTest extends Specification {
    MemberService testObj

    def setup() {
        testObj = new MemberService()
    }

    /*
    BigDecimal calculateNetAmount(BigDecimal grossAmount, BigDecimal vatRate) {
        BigDecimal vatAsFraction = vatRate.divide(new BigDecimal(100));
        return grossAmount.divide(new BigDecimal(1).add(vatAsFraction).setScale(12), BigDecimal.ROUND_HALF_EVEN).setScale(12);
    }
     */

    def calculateNetAmount() {
        expect:
        testObj.calculateNetAmount(new BigDecimal("2000"), new BigDecimal("20")) == new BigDecimal("1666.67")
    }
}
