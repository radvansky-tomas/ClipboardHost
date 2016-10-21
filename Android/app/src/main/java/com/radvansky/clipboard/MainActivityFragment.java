package com.radvansky.clipboard;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.orhanobut.hawk.Hawk;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private SearchView searchView;
    private EditText clipText;
    private MyClipboardManager manager;
    private LinearLayout searchBar;
    private File currentFile;
    private Handler handler = new Handler();
    int offset = 0;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        clipText = (EditText) view.findViewById(R.id.clipText);
        searchBar = (LinearLayout) view.findViewById(R.id.searchBar);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                offset = 0;
                searchBar.setVisibility(View.GONE);
                clipText.setSelection(0,0);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset = 0;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                offset = 0;
                return false;
            }
        });

        Button nextBtn = (Button) view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText(searchView.getQuery().toString());
            }
        });

        Button pgBtn = (Button) view.findViewById(R.id.pgBtn);
        pgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectParagraph();
            }
        });

        Button allBtn = (Button) view.findViewById(R.id.allBtn);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllContent();
            }
        });

        Button rowBtn = (Button) view.findViewById(R.id.rowBtn);
        rowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLine();
            }
        });

        Button replaceBtn = (Button) view.findViewById(R.id.replaceBtn);
        replaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceSearch();
            }
        });

        Button copyBtn = (Button) view.findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clipText.getSelectionStart()>=0 && clipText.getSelectionEnd()>0)
                {
                    String selectedText = clipText.getText().subSequence(clipText.getSelectionStart(),clipText.getSelectionEnd()).toString();
                    Log.e("paragraph",selectedText);
                    manager.copyToClipboard(getActivity(),selectedText);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), "Selection has been saved in clipboard", Toast.LENGTH_SHORT).show();
                        }
                    },750);
                }
            }
        });

        manager = new MyClipboardManager();
        return view;
    }


    public void createNewFile() {
        clipText.setText("");
        currentFile = null;
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("New file");
    }

    public void openFile(String fileName) {
        try {
            currentFile = new File(getActivity().getFilesDir(), fileName);
            clipText.setText(Helpers.getStringFromFile(currentFile.getPath()));
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFile() {
        if (clipText.getText().length() != 0) {
            if (currentFile == null) {
                //Dialog
                new MaterialDialog.Builder(getActivity())
                        .title("Save As")
                        .content("Please enter new file name!")
                        .input("File name", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                currentFile = new File(getActivity().getFilesDir(),input.toString());
                                if (!currentFile.exists()) {
                                    saveDataFile(clipText.getText().toString(), currentFile);
                                }
                                else
                                {
                                    currentFile = null;
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Error")
                                            .content("Filename '" + input.toString() + "' already exists!")
                                            .positiveText("Re-enter")
                                            .negativeText("Cancel")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    saveFile();
                                                }
                                            })
                                            .show();
                                }
                            }
                        }).show();
            } else {
                //Overwrite
                saveDataFile(clipText.getText().toString(), currentFile);
            }
        }
        else
        {
            new MaterialDialog.Builder(getActivity())
                    .title("Error")
                    .content("Empty document!")
                    .positiveText(android.R.string.ok)
                    .show();
        }
    }

    private void replaceSearch()
    {
        if(clipText.getSelectionStart()>=0 && clipText.getSelectionEnd()>0) {
            String selectedText = clipText.getText().subSequence(clipText.getSelectionStart(), clipText.getSelectionEnd()).toString();

            new MaterialDialog.Builder(getActivity())
                    .title("Replace")
                    .content("Replace string:'" + selectedText +"'")
            .input("String to replace", "", new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog dialog, CharSequence input) {
                    // Do something
                    if (input.length()>0) {
                        clipText.getText().replace(clipText.getSelectionStart(), clipText.getSelectionEnd(), input);
                    }
                }
            }).show();
        }
    }

    private void saveDataFile(String data, File file) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data.getBytes());
            stream.close();
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(file.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void searchText(final String newText) {
        Log.d("searchText", "Text:" + clipText.getText().toString());
        final int start = clipText.getText().toString().indexOf(newText, offset);
        Log.d("searchText", "Query:" + newText);
        Log.d("searchText", "Start:" + start);
        Log.d("searchText", "Offset:" + offset);

        if (start != -1) {
            offset = start + newText.length() - 1;
            clipText.post(new Runnable() {
                @Override
                public void run() {
                    clipText.requestFocus();
                    clipText.setSelection(0);
                    clipText.setSelection(start, start + newText.length());
                }
            });
        } else {
            if (offset != 0) {
                offset = 0;
                searchText(newText);
            }
        }
    }

    private void selectAllContent() {
        if (clipText.getText().length() >0)
        {
            clipText.setSelection(0,clipText.length()-1);
            String selectedText = clipText.getText().subSequence(clipText.getSelectionStart(),clipText.getSelectionEnd()).toString();
            Log.e("all",selectedText);
            manager.copyToClipboard(getActivity(),selectedText);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clipText.setSelection(0,0);
                    Toast.makeText(getActivity().getBaseContext(), "All content has been saved in clipboard", Toast.LENGTH_SHORT).show();
                }
            },750);
        }
    }

    private void selectLine()
    {
        int selectionStart = Selection.getSelectionStart(clipText.getText());
        Layout layout = clipText.getLayout();
        if (!(selectionStart == -1)) {
            int line = layout.getLineForOffset(selectionStart);
            clipText.setSelection(layout.getLineStart(line),layout.getLineEnd(line));
            String selectedText = clipText.getText().subSequence(clipText.getSelectionStart(),clipText.getSelectionEnd()).toString();
            Log.e("line",selectedText);
            manager.copyToClipboard(getActivity(),selectedText);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clipText.setSelection(0,0);
                    Toast.makeText(getActivity().getBaseContext(), "Line has been saved in clipboard", Toast.LENGTH_SHORT).show();
                }
            },750);
        }
    }
    private void selectParagraph() {
        Log.d("searchText", "Text:" + clipText.getText().toString());
        int cursor = 0;
        cursor = clipText.getSelectionStart();
        int first = 0;
        int oldFirst = 0;
        int last = clipText.getText().toString().indexOf("\n\n", cursor);
        if (last != -1) {
            while (first <= cursor) {
                if (first != 0) {
                    first = first + 1;
                }
                first = clipText.getText().toString().indexOf("\n\n", first);
                if (first <= cursor && first>0) {
                    oldFirst = first;
                }
            }
        } else {
            while (first <= clipText.getText().length() && first != -1) {
                if (first != 0) {
                    first = first + 1;
                }
                first = clipText.getText().toString().indexOf("\n\n", first);
                if (first <= clipText.getText().length() && first>0) {
                    oldFirst = first;
                }
            }
        }
        int startCursor = 0;
        int endCursor = 0;
        if (oldFirst != 0) {
            startCursor = clipText.getText().toString().indexOf("\n\n", oldFirst);
        }


        if (last != -1) {
            endCursor = clipText.getText().toString().indexOf("\n\n", startCursor + 1);
        } else {
            endCursor = clipText.getText().length();
        }

        //try to find end
        final int start = startCursor;
        final int end = endCursor;

        Log.d("searchText", "Start:" + start);
        Log.d("searchText", "Cursor:" + cursor);
        Log.d("searchText", "End:" + end);
        if (start != -1) {
            if (end != -1) {
                clipText.post(new Runnable() {
                    @Override
                    public void run() {
                        clipText.requestFocus();
                        clipText.setSelection(0);
                        if (start == 0) {
                            clipText.setSelection(0, end);
                        } else {
                            clipText.setSelection(start + 2, end);
                        }
                        sendSelectedText();
                    }
                });
            } else {
                clipText.post(new Runnable() {
                    @Override
                    public void run() {
                        clipText.requestFocus();
                        clipText.setSelection(0);
                        clipText.setSelection(start, clipText.getText().length());
                        sendSelectedText();
                    }
                });
            }
        }

    }

    private void sendSelectedText()
    {
        if(clipText.getSelectionStart()>=0 && clipText.getSelectionEnd()>0)
        {
            String selectedText = clipText.getText().subSequence(clipText.getSelectionStart(),clipText.getSelectionEnd()).toString();
            Log.e("paragraph",selectedText);
            manager.copyToClipboard(getActivity(),selectedText);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clipText.setSelection(0,0);
                    Toast.makeText(getActivity().getBaseContext(), "Paragraph has been saved in clipboard", Toast.LENGTH_SHORT).show();
                }
            },750);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search: {
                if (searchBar.getVisibility() == View.GONE) {
                    searchBar.setVisibility(View.VISIBLE);
                } else {
                    searchBar.setVisibility(View.GONE);
                }
                return true;
            }

            case R.id.action_save:
            {
                saveFile();
                return  true;
            }

            case R.id.action_saveas:
            {
                if (currentFile != null)
                {
                    new MaterialDialog.Builder(getActivity())
                            .title("Save As")
                            .content("Please enter new file name!")
                            .input("File name", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // Do something
                                    currentFile = new File(getActivity().getFilesDir(),input.toString());
                                    if (!currentFile.exists()) {
                                        saveDataFile(clipText.getText().toString(), currentFile);
                                    }
                                    else
                                    {
                                        currentFile = null;
                                        new MaterialDialog.Builder(getActivity())
                                                .title("Error")
                                                .content("Filename '" + input.toString() + "' already exists!")
                                                .positiveText("Re-enter")
                                                .negativeText("Cancel")
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        saveFile();
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            }).show();
                }
                else
                {
                    new MaterialDialog.Builder(getActivity())
                            .title("Error")
                            .content("There is no openned file!")
                            .positiveText(android.R.string.ok)
                            .show();
                }
                return  true;
            }

            case R.id.action_delete:
            {
                if (currentFile != null)
                {
                    new MaterialDialog.Builder(getActivity())
                            .title("Delete File")
                            .content("Are you sure?")
                            .positiveText(android.R.string.yes)
                            .negativeText(android.R.string.no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    currentFile.delete();
                                    createNewFile();
                                }
                            })
                            .show();
                }
                else
                {
                    new MaterialDialog.Builder(getActivity())
                            .title("Error")
                            .content("There is no openned file!")
                            .positiveText(android.R.string.ok)
                            .show();
                }
                return  true;
            }

            case R.id.action_file:;
            {
                // This always works
                Intent i = new Intent(getContext(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                getActivity().startActivityForResult(i, 100);
                return true;
            }

            case R.id.action_dir:;
            {
                // This always works
                Intent i = new Intent(getContext(), FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

                // Configure initial directory by specifying a String.
                // You could specify a String like "/storage/emulated/0/", but that can
                // dangerous. Always use Android's API calls to get paths to the SD-card or
                // internal memory.
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                getActivity().startActivityForResult(i, 200);
                return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
