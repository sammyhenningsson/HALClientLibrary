package se.sammygadd.library.halclient.layouts;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import se.sammygadd.library.halclient.Constants;
import se.sammygadd.library.halclient.HalContainer;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.ValidationError;

public class FormLayout extends HalLayout {
    private Form mForm;
    private ValidationError mValidationError;
    private HalContainer.OnSubmitFormListener mOnSubmitFormListener;

    private class InputWatcher implements TextWatcher {
        private Form.Field mField;

        public InputWatcher(Form.Field field) {
            mField = field;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mField.setValue(s.toString());
        }
    }

    public FormLayout(
            Context context,
            Form form,
            HalContainer.OnNavigateToListener navigateToListener,
            HalContainer.OnSubmitFormListener submitFormListener
    ) {
        this(context, form, null, navigateToListener, submitFormListener);
    }

    public FormLayout(
            Context context,
            Form form,
            ValidationError validationError,
            HalContainer.OnNavigateToListener navigateToListener,
            HalContainer.OnSubmitFormListener submitFormListener
    ) {
        super(context);
        mForm = form;
        mValidationError = validationError;
        mOnNavigateToListener = navigateToListener;
        mOnSubmitFormListener = submitFormListener;
        createForm();
    }

    public Form getForm() {
        return mForm;
    }

    private void createForm() {
        addTitle();
        addFields();
        addButton();
    }

    private void addTitle() {
        TextView title = new TextView(getContext());
        title.setLayoutParams(generateDefaultLayoutParams());
        title.setText(mForm.getTitle());
        addView(title);
    }

    private void addFields() {
        for (Form.Field field: mForm.getFields()) {
            if (!field.isHidden()) {
                addView(createInput(field));
            }
        }
    }

    private void addButton() {
        Button button = new Button(getContext());
        button.setLayoutParams(generateDefaultLayoutParams());
        button.setText(mForm.getSubmit());
        button.setOnClickListener(view -> submitForm());
        addView(button);
    }

    private void submitForm() {
        if (mOnSubmitFormListener != null) {
            Log.d(Constants.TAG, "calling submit listener..");
            mOnSubmitFormListener.onSubmitForm(mForm);
        } else {
            Log.d(Constants.TAG, "no listener for submitForm");
        }
    }

    private String getError(String name) {
        if (mValidationError == null) {
            return null;
        } else  {
            return mValidationError.getMessage(name);
        }
    }


    private View createInput(Form.Field field) {
        TextInputLayout inputLayout = new TextInputLayout(getContext());
        inputLayout.setHint(field.getLabel());
        inputLayout.setError(getError(field.getName()));

        TextInputEditText input = new TextInputEditText(inputLayout.getContext());
        input.setLayoutParams(generateDefaultLayoutParams());
        int inputType = getInputType(field.getType());
        input.setInputType(inputType);
        input.setText(field.getValue());
        input.addTextChangedListener(new InputWatcher(field));

        inputLayout.addView(input);
        return inputLayout;
    }

    private int getInputType(int type) {
        switch (type) {
            case Form.Field.STRING:
                return InputType.TYPE_CLASS_TEXT;
            case Form.Field.INTEGER:
                return InputType.TYPE_CLASS_NUMBER;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

}
