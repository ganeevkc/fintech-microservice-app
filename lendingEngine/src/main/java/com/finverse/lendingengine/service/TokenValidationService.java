package com.finverse.lendingengine.service;

import com.finverse.lendingengine.model.User;

public interface TokenValidationService {
    User validateToken(final String token);
}
