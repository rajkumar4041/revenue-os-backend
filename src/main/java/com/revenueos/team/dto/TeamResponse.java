package com.revenueos.team.dto;

import java.util.UUID;

public record TeamResponse(UUID id, String name, UUID managerUserId) {
}
