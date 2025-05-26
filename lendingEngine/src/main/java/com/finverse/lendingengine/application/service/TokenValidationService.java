package com.finverse.lendingengine.application.service;

import com.finverse.lendingengine.domain.model.User;

public interface TokenValidationService {
    User validateToken(final String token);
}
