package com.coop.loan;

import com.coop.loan.service.SocialSecurityNumberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocialSecurityNumberServiceTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "37605030299",  // male 1976
            "50001011352",   // male 2000
            "49403136515",  // female 1994
            "38001085718",  // male 1980
            "49002124226",  // female 1990
            "51001091072"   // male 2010
    })
    void isValid_validCodes_returnsTrue(String code) {
        assertThat(SocialSecurityNumberService.isValid(code)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678901",  // invalid checksum
            "99999999999",  // invalid century digit
            "3760503029",   // too short
            "376050302991", // too long
            "3760503029A",  // non-numeric
            ""              // empty
    })
    void isValid_invalidCodes_returnsFalse(String code) {
        assertThat(SocialSecurityNumberService.isValid(code)).isFalse();
    }

    @Test
    void extractAge_19xxBirthYear_returnsCorrectAge() {
        // born 1976 should return 48 or 49 depending on day
        int age = SocialSecurityNumberService.extractAge("37605030299");
        assertThat(age).isBetween(49, 50);
    }

    @Test
    void extractAge_20xxBirthYear_returnsCorrectAge() {
        // born 2000
        int age = SocialSecurityNumberService.extractAge("50001011352");
        assertThat(age).isBetween(25, 26);
    }

    @Test
    void extractAge_invalidCentury_throwsException() {
        assertThatThrownBy(() -> SocialSecurityNumberService.extractAge("97605030299"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void extractAge_leapYearBirthday_calculatesCorrectly() {
        // born 29 Feb 2000 (leap year)
        int age = SocialSecurityNumberService.extractAge("50002290004");
        assertThat(age).isBetween(25, 26);
    }

    @Test
    void extractAge_18xxBirthYear_returnsCorrectAge() {
        int age = SocialSecurityNumberService.extractAge("16001010006");
        assertThat(age).isGreaterThan(100);
    }
}
