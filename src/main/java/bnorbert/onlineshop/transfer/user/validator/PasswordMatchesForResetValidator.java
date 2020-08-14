package bnorbert.onlineshop.transfer.user.validator;

import bnorbert.onlineshop.transfer.user.request.ResetPasswordRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesForResetValidator implements ConstraintValidator<PasswordMatchesForReset, Object> {
    @Override
    public void initialize(final PasswordMatchesForReset constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final ResetPasswordRequest user = (ResetPasswordRequest) obj;
        return user.getPassword().equals(user.getPasswordConfirm());
    }

}