package bnorbert.onlineshop.transfer.user.validator;

import bnorbert.onlineshop.transfer.user.request.SaveUserRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
    }

    @Override
        public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
            final SaveUserRequest user = (SaveUserRequest) obj;
            return user.getPassword().equals(user.getPasswordConfirm());
    }

}