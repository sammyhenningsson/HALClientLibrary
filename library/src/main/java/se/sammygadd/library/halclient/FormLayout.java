package se.sammygadd.library.halclient;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class FormLayout extends LinearHalLayout {
    private Form mForm;

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

    public FormLayout(Context context, Form form) {
        super(context, form);
        mForm = form;
        createForm();
    }

    private void createForm() {
        addTitle();
        addFields();
        addButton();
    }

    private void addTitle() {
        TextView title = new TextView(mContext);
        title.setLayoutParams(generateDefaultLayoutParams());
        title.setText(mForm.getTitle());
        addView(title);
    }

    private void addFields() {
        for (Form.Field field: mForm.getFields()) {
            addView(createInput(field));
        }
    }

    private void addButton() {
        Button button = new Button(mContext);
        button.setLayoutParams(generateDefaultLayoutParams());
        button.setText(mForm.getSubmit());
        button.setOnClickListener(form -> mForm.submit());
        addView(button);
    }

    private View createInput(Form.Field field) {
        TextInputLayout inputLayout = new TextInputLayout(mContext);
        inputLayout.setHint(field.getLabel());

        EditText input = new EditText(mContext);
        input.setLayoutParams(generateDefaultLayoutParams());
        int inputType = getInputType(field.getType());
        input.setInputType(inputType);
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
