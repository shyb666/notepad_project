/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notepad;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.asd.R;

public class TitleEditor extends Activity {

    public static final String EDIT_TITLE_ACTION = "com.android.notepad.action.EDIT_TITLE";

    // Creates a projection that returns the note ID and the note contents.
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_TYPE //
    };

    // The position of the title column in a Cursor returned by the provider.
    private static final int COLUMN_INDEX_TITLE = 1;

    // A Cursor object that will contain the results of querying the provider for a note.
    private Cursor mCursor;

    // An EditText object for preserving the edited title.
    private EditText mText;

    // A URI object for the note whose title is being edited.
    private Uri mUri;

    private Button type1,type2,type3,type4,type5;
    private String selected_type;

    /**
     * This method is called by Android when the Activity is first started. From the incoming
     * Intent, it determines what kind of editing is desired, and then does it.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_editor);
        mUri = getIntent().getData();
        mCursor = managedQuery(
            mUri,        // The URI for the note that is to be retrieved.
            PROJECTION,  // The columns to retrieve
            null,        // No selection criteria are used, so no where columns are needed.
            null,        // No where columns are used, so no where values are needed.
            null         // No sort order is needed.
        );

        // Gets the View ID for the EditText box
        mText = (EditText) this.findViewById(R.id.title);
        type1 =this.findViewById(R.id.work);
        type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理点击事件
               selected_type="work";
            }
        });
        type2 =this.findViewById(R.id.study);
        type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理点击事件
                selected_type="study";
            }
        });
        type3 =this.findViewById(R.id.daylylife);
        type3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理点击事件
                selected_type="daylylife";
            }
        });
        type4 =this.findViewById(R.id.privacy);
        type4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理点击事件
                selected_type="privacy";
            }
        });
        type5 =this.findViewById(R.id.other);
        type5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理点击事件
                selected_type="other";
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mCursor != null) {
            mCursor.moveToFirst();
            mText.setText(mCursor.getString(COLUMN_INDEX_TITLE));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mCursor != null) {
            ContentValues values = new ContentValues();
            values.put(NotePad.Notes.COLUMN_NAME_TITLE, mText.getText().toString());

            //设置类型
            values.put(NotePad.Notes.COLUMN_TYPE, selected_type);
            getContentResolver().update(
                mUri,    // The URI for the note to update.
                values,  // The values map containing the columns to update and the values to use.
                null,    // No selection criteria is used, so no "where" columns are needed.
                null     // No "where" columns are used, so no "where" values are needed.
            );

        }
    }

    public void onClickOk(View v) {
        finish();
    }
}
