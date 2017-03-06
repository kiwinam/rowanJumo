package smart.rowan.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import smart.rowan.R;


public class EmployeeMemoDialog extends Dialog implements View.OnClickListener {
    private static final int DIALOG_VIEW = R.layout.dialog_employee_memo;
    private String name;
    private Context context;

    public EmployeeMemoDialog(Context context, String name) {
        super(context);
        this.name = name;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(DIALOG_VIEW);
        TextView employeeNameTextView = (TextView) findViewById(R.id.employeeNameTextView);
        employeeNameTextView.setText(name);
        Button cancelBtn = (Button) findViewById(R.id.memoCancelBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memoCancelBtn:
                cancel();
                break;
        }
    }
}