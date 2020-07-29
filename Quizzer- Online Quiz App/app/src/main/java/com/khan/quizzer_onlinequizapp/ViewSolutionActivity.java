package com.khan.quizzer_onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.util.List;

public class ViewSolutionActivity extends AppCompatActivity {
    private static int FROM_SINGLE_PDF = 0;
    private static int FROM_ADMISSION_PREPARATION = 1;
    private static int FROM_VIEW_SOLUTIOM = 2;

    private ProgressBar progressBar;
    private PDFView pdfView;
    private Button medicalBtn, engineeringBtn, publicBtn, privateBtn, mcqBtn, cqBtn;
    private String urlMedical, urlEngineering, urlPublic, urlPrivate, mcqUrl, cqUrl, currentUrl, singlePdfUrl;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout buttomLayout;
    private SeekBar seekBar;
    private TextView pages, errorMsgPdf;
    private ImageView reloadPdf;
    private int currentPgCount, totalPgCount;
    private int selectedItem;
    private Switch nightMode, horizontalSwipe;
    private Boolean isNightMode, isHorizontalSwipe = false, isLoading;
    private int type;
    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_solution);
        initView();
        setPdfURL();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            initType();
        }

        Dexter.withActivity(ViewSolutionActivity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            loadPdf(1);
                            selectItemHandle();
                            buttomLayoutHandle();
                        } else {
                            errorMsgPdf.setVisibility(View.VISIBLE);
                            Toast.makeText(ViewSolutionActivity.this, "You have denied some permissions permanently, if the app force close try granting permission from settings.\"", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void initType() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            isNightMode = true;
            nightMode.setChecked(true);
        } else {
            isNightMode = false;
            nightMode.setChecked(false);
        }

        if (type == FROM_ADMISSION_PREPARATION) {
            currentUrl = urlMedical;
            selectedItem = 2;
            medicalBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
            medicalBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        } else if (type == FROM_VIEW_SOLUTIOM) {
            currentUrl = mcqUrl;
            selectedItem = 0;
            mcqBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
            mcqBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        } else if (type == FROM_SINGLE_PDF) {
            horizontalScrollView.setVisibility(View.GONE);
            currentUrl = singlePdfUrl;
            // selectedItem = -1;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUrl", currentUrl);
        outState.putBoolean("isNightMode", isNightMode);
        outState.putBoolean("isHorizontalSwipe", isHorizontalSwipe);
        outState.putInt("currentPgCount", currentPgCount);
        outState.putInt("selectedItem", selectedItem);
        outState.putInt("flag", flag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentUrl = savedInstanceState.getString("currentUrl");
        isNightMode = savedInstanceState.getBoolean("isNightMode");
        isHorizontalSwipe = savedInstanceState.getBoolean("isHorizontalSwipe");
        currentPgCount = savedInstanceState.getInt("currentPgCount");
        selectedItem = savedInstanceState.getInt("selectedItem");
        flag = savedInstanceState.getInt("flag");

        if (selectedItem == 2) {
            medicalBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
            medicalBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        } else if (selectedItem == 3) {
            engineeringBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
            engineeringBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        } else if (selectedItem == 4) {
            publicBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
            publicBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        } else if (selectedItem == 5) {
            privateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
            privateBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        }
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        pdfView = findViewById(R.id.pdfView);
        mcqBtn = findViewById(R.id.mcq);
        cqBtn = findViewById(R.id.cq);
        medicalBtn = findViewById(R.id.medical);
        engineeringBtn = findViewById(R.id.engineering);
        publicBtn = findViewById(R.id.publicBtn);
        privateBtn = findViewById(R.id.privateBtn);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        buttomLayout = findViewById(R.id.buttom_layout);
        seekBar = findViewById(R.id.seekBar);
        pages = findViewById(R.id.pages);
        nightMode = findViewById(R.id.night_mode);
        horizontalSwipe = findViewById(R.id.horizontal_swipe);
        reloadPdf = findViewById(R.id.reload_pdf);
        errorMsgPdf = findViewById(R.id.pdf_error_msg);

        type = getIntent().getIntExtra("type", 0);
    }

    private void setPdfURL() {
        singlePdfUrl = getIntent().getStringExtra("singlePdfUrl");
        urlMedical = getIntent().getStringExtra("urlMedical");
        urlEngineering = getIntent().getStringExtra("urlEngineering");
        urlPublic = getIntent().getStringExtra("urlPublic");
        urlPrivate = getIntent().getStringExtra("urlPrivate");
        mcqUrl = getIntent().getStringExtra("mcqUrl");
        cqUrl = getIntent().getStringExtra("cqUrl");

        if (mcqUrl == null || mcqUrl.isEmpty()) {
            mcqBtn.setVisibility(View.GONE);

        } else {
            mcqBtn.setVisibility(View.VISIBLE);
            flag = 0;
        }

        if (cqUrl == null || cqUrl.isEmpty()) {
            cqBtn.setVisibility(View.GONE);
        } else {
            cqBtn.setVisibility(View.VISIBLE);
            flag = 0;
        }

        if (urlEngineering == null || urlEngineering.isEmpty()) {
            engineeringBtn.setVisibility(View.GONE);
        } else {
            engineeringBtn.setVisibility(View.VISIBLE);
            flag = 0;
        }

        if (urlMedical == null || urlMedical.isEmpty()) {
            medicalBtn.setVisibility(View.GONE);
        } else {
            medicalBtn.setVisibility(View.VISIBLE);
            flag = 0;
        }

        if (urlPublic == null || urlPublic.isEmpty()) {
            publicBtn.setVisibility(View.GONE);
        } else {
            publicBtn.setVisibility(View.VISIBLE);
            flag = 0;
        }

        if (urlPrivate == null || urlPrivate.isEmpty()) {
            privateBtn.setVisibility(View.GONE);
        } else {
            privateBtn.setVisibility(View.VISIBLE);
            flag = 0;
        }

        if (flag == 1) {
            finish();
            Toast.makeText(this, "Pdf will be uploaded soon.", Toast.LENGTH_SHORT).show();
        }
    }

    private void buttomLayoutHandle() {
        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isLoading) {
                    if (b) {
                        isNightMode = true;
                        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        isNightMode = false;
                        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }
        });

        horizontalSwipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isLoading) {
                    if (b) {
                        isHorizontalSwipe = true;
                        loadPdf(2);
                    } else {
                        isHorizontalSwipe = false;
                        loadPdf(2);
                    }
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isLoading == false && b == true) {
                    pdfView.jumpTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        reloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadPdf.setVisibility(View.GONE);
                loadPdf(1);
            }
        });
    }

    private void loadPdf(final int loadType) {
        if (loadType == 1) {
            currentPgCount = 1;
            progressBar.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.GONE);
            isLoading = true;
        }
        pages.setText("... ...");

        FileLoader.with(this)
                .load(currentUrl)
                .fromDirectory("Anunad Academy", FileLoader.DIR_EXTERNAL_PUBLIC)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        File pdfFile = response.getBody();
                        pdfView.fromFile(pdfFile)
                                .enableSwipe(true)
                                .swipeHorizontal(isHorizontalSwipe)
                                .enableDoubletap(true)
                                .defaultPage(currentPgCount - 1)
                                .enableAnnotationRendering(false)
                                .password(null)
                                .scrollHandle(null)
                                .onRender(new OnRenderListener() {
                                    @Override
                                    public void onInitiallyRendered(int nbPages) {
                                        // pdfView.fitToWidth(0);
                                        totalPgCount = nbPages;
                                        pages.setText(currentPgCount + " / " + totalPgCount);
                                        seekBar.setMax(nbPages - 1);
                                        seekBar.setProgress(currentPgCount - 1);
                                        nightMode.setChecked(isNightMode);
                                        horizontalSwipe.setChecked(isHorizontalSwipe);
                                    }
                                })
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        currentPgCount = page + 1;
                                        pages.setText(currentPgCount + " / " + totalPgCount);
                                        seekBar.setProgress(page);
                                    }
                                })
                                .onTap(new OnTapListener() {
                                    @Override
                                    public boolean onTap(MotionEvent e) {
                                        if (horizontalScrollView.getVisibility() == View.VISIBLE) {
                                            horizontalScrollView.setVisibility(View.GONE);
                                            buttomLayout.setVisibility(View.GONE);
                                        } else {
                                            horizontalScrollView.setVisibility(View.VISIBLE);
                                            buttomLayout.setVisibility(View.VISIBLE);
                                        }
                                        return false;
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        reloadPdf.setVisibility(View.VISIBLE);
                                        Toast.makeText(ViewSolutionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .enableAntialiasing(true)
                                .spacing(2)
                                .autoSpacing(false)
                                .fitEachPage(false)
                                // .pageFitPolicy(FitPolicy.WIDTH)
                                .pageSnap(isHorizontalSwipe)
                                .pageFling(false)
                                .nightMode(isNightMode)
                                .load();
                        pdfView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        reloadPdf.setVisibility(View.VISIBLE);
                        if (flag == 0) {
                            Toast.makeText(ViewSolutionActivity.this, "Something went wrong. Can't load the pdf.", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    private void selectItemHandle() {
        medicalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUrl = urlMedical;
                selectedItem = 2;
                loadPdf(1);
                medicalBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
                medicalBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                engineeringBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                engineeringBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                publicBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                publicBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                privateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                privateBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
            }
        });
        engineeringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUrl = urlEngineering;
                selectedItem = 3;
                loadPdf(1);
                medicalBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                medicalBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                engineeringBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
                engineeringBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                publicBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                publicBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                privateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                privateBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
            }
        });
        publicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUrl = urlPublic;
                selectedItem = 4;
                loadPdf(1);
                medicalBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                medicalBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                engineeringBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                engineeringBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                publicBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
                publicBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                privateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                privateBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
            }
        });
        privateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUrl = urlPrivate;
                selectedItem = 5;
                loadPdf(1);
                medicalBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                medicalBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                engineeringBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                engineeringBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                publicBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorGeneralBg)));
                publicBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                privateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPdfSelectorBg)));
                privateBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            }
        });

    }

}