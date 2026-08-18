import { apiRequest } from "./api";

export function requestVerificationCode(email) {
  return apiRequest("/auth/code", {
    method: "POST",
    requiresAuth: false,

    body: {
      email,
    },
  });
}

export function verifyVerificationCode(email, code) {
  return apiRequest("/auth/verify", {
    method: "POST",
    requiresAuth: false,

    body: {
      email,
      code,
    },
  });
}

export function getAuthenticatedUser() {
  return apiRequest("/auth/me", {
    method: "GET",
  });
}
