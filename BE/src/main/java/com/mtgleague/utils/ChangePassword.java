package com.mtgleague.utils;

public record ChangePassword(Integer otp, String newPassword, String confirmPassword) {
}
