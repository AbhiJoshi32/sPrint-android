package com.binktec.sprint.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.utility.Misc;
import com.binktec.sprint.utility.SessionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrintJobDetailActivity extends AppCompatActivity {

    private static final String TAG = "Details";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fileList)
    TextView fileList;
    @BindView(R.id.statusText)
    TextView statusText;
    @BindView(R.id.shopName)
    TextView shopName;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.dateText)
    TextView dateText;
    @BindView(R.id.requestTime)
    TextView requestTime;
    @BindView(R.id.printTime)
    TextView printTime;
    @BindView(R.id.bindingText)
    TextView bindingText;
    @BindView(R.id.costPerPage)
    TextView costPerPage;
    @BindView(R.id.printingCostText)
    TextView printingCostText;
    @BindView(R.id.bindingCostText)
    TextView bindingCostText;
    @BindView(R.id.totalCostText)
    TextView totalCostText;
    @BindView(R.id.total_pages)
    TextView totalPages;
    @BindView(R.id.textView29)
    TextView textView29;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_job_detail);
        ButterKnife.bind(this);
        PrintJobDetail printJobDetail = SessionManager.getClickedPrintJobDetail();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        statusText.setText(printJobDetail.getStatus());
        shopName.setText(printJobDetail.getPrintTransaction().getShop().getShopName());
        location.setText(printJobDetail.getPrintTransaction().getShop().getShopLocation());
        dateText.setText(printJobDetail.getIssuedDate());
        requestTime.setText(printJobDetail.getIssuedTime());
        float cost = Misc.costPerPage(printJobDetail.getPrintTransaction().getPrintDetail(),
                printJobDetail.getPrintTransaction().getShop());
        costPerPage.setText(Float.toString(cost));
        float print = printJobDetail.getPrintTransaction().getPrintCost();
        float bind = printJobDetail.getPrintTransaction().getBindingCost();
        bindingText.setText(printJobDetail.getPrintTransaction().getPrintDetail().getBindingType());
        bindingCostText.setText(Float.toString(bind));
        printingCostText.setText(Float.toString(print));
        float totalCost = print + bind;
        totalCostText.setText(Float.toString(totalCost));
        totalPages.setText(Integer.toString(printJobDetail.getPrintTransaction().getPrintDetail().getPagesToPrint()));
        List<FileDetail> fileDetails = printJobDetail.getPrintTransaction().getFileDetails();
        if (printJobDetail.getCompletedTime() == null) {
            printTime.setVisibility(View.GONE);
            textView29.setVisibility(View.GONE);
        } else {
            printTime.setVisibility(View.VISIBLE);
            textView29.setVisibility(View.VISIBLE);
            printTime.setText(printJobDetail.getCompletedTime());
        }
        String fileListText = "";
        for (FileDetail fileDetail : fileDetails) {
            fileListText += fileDetail.getFilename() + "\n";
        }
        fileListText = fileListText.trim();
        fileList.setText(fileListText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
