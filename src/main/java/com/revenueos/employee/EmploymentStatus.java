package com.revenueos.employee;

/** The people lifecycle: onboarding → active → (leave) → offboarding → offboarded. */
public enum EmploymentStatus {
    ONBOARDING,
    ACTIVE,
    ON_LEAVE,
    OFFBOARDING,
    OFFBOARDED
}
