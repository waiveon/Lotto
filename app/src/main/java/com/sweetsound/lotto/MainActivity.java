package com.sweetsound.lotto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText mInputEdittext;
    private RecyclerView mRecyclerView;

    private LottoAdapter mLottoAdapter;

    private ArrayList<NLottoConn.NLottoResponse> mItemList;

    private boolean mIsAutoScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mLottoAdapter = new LottoAdapter();

        mRecyclerView = (RecyclerView) findViewById(R.id.result_listview);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mLottoAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int position = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                if (position > 0) {
                    Log.e("TAG", "LJS== position : " + position);
                    callLotto((position + 1) + "", false);
                }
            }
        });

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mInputEdittext = (EditText) findViewById(R.id.input_edittext);
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberStr = mInputEdittext.getText().toString();
                mInputEdittext.setText("");

                if (TextUtils.isDigitsOnly(numberStr) == false) {
                    Toast.makeText(MainActivity.this, "숫자를 입력해 주세요", Toast.LENGTH_SHORT).show();

                    return;
                }

                callLotto(numberStr, true);
            }
        });

        callLotto("", true);
    }

    private void callLotto(String number, boolean isAutoScroll) {
        mIsAutoScroll = isAutoScroll;
        Call<NLottoConn.NLottoResponse> call = NLottoConn.getLottoNumbers(number);
        call.enqueue(mCallback);
    }

    Callback<NLottoConn.NLottoResponse> mCallback = new Callback<NLottoConn.NLottoResponse>() {
        @Override
        public void onResponse(@NonNull Call<NLottoConn.NLottoResponse> call,
                               @NonNull Response<NLottoConn.NLottoResponse> response) {
            NLottoConn.NLottoResponse body = response.body();

            if (body != null) {
                if (mItemList == null) {
                    mItemList = new ArrayList<NLottoConn.NLottoResponse>(Collections.nCopies(Integer.parseInt(body.drwNo), new NLottoConn.NLottoResponse()));

                    mLottoAdapter.setItem(mItemList);
                }

                int insertIndex = Integer.parseInt(body.drwNo) -1;

                mItemList.set(insertIndex, body);
                mLottoAdapter.notifyItemChanged(insertIndex);

                if (mIsAutoScroll == true) {
                    mRecyclerView.scrollToPosition(insertIndex);

                    mIsAutoScroll = false;
                }
            } else {
                String err = "(unknown)";

                try {
                    err = response.errorBody().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "로또 정보 가져오기 실패: " + err, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<NLottoConn.NLottoResponse> call,
                              @NonNull Throwable t) {
            Toast.makeText(MainActivity.this, "로또 정보 가져오기 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}
