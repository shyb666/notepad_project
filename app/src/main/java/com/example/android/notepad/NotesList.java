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
import android.widget.PopupMenu;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.asd.R;

public class NotesList extends ListActivity {

    // For logging and debugging
    private static final String TAG = "NotesList";
    //输入框
    private EditText editText;
    //搜素按键
     private ImageButton b1;
     //添加按键
    private ImageButton b2;
    //记录b2偏移量
    private float dX_b2, dY_b2;
    //存放添加按钮的fragment
    private FrameLayout container;
    //分类选择按钮
    private Button select_type;
    //显示笔记类型的图标
    private ImageView type_image;
    //排序方式
    private Button order_way;
    //升序降序
    private ImageButton up_down;
    /**
     * The columns needed by the cursor adapter
     */

    //定义需要的列
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_CREATE_DATE,//2
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,//3
            NotePad.Notes.COLUMN_TYPE
            
            //新增封面
          //  NotePad.Notes.COLUMN_COVER
    };

    //实际排列顺序（升序降序）
    private String final_order1=NotePad.Notes.DEFAULT_SORT_ORDER_TYPE;
    private String final_order2=NotePad.Notes.DEFAULT_SORT_ORDER_UP;
    private String final_order;
    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;

    private static final int COLUMN_NAME_CREATE_DATE= 2;

    private static final int COLUMN_NAME_MODIFICATION_DATE= 3;

    private static final int COLUMN_COVER=4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        setContentView(R.layout.note_mainpage);
        getListView().setOnCreateContextMenuListener(this);
//搜索框
        editText=findViewById(R.id.input);
//排序方式
          order_way=findViewById(R.id.order_ways);
          order_way.setText("根据修改时间");
          order_way.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //设为根据标题排序
                  if(order_way.getText().toString()=="根据修改时间") {
                      order_way.setText("根据标题");
                      final_order1 = "title ";

                      init_list();
                  }
                  //设为根据创建时间排序
                  else if(order_way.getText().toString()=="根据标题") {
                      order_way.setText("根据创建时间");
                      final_order1 = "created ";
                      init_list();
                  }
                  //设为根据更新时间
                  else if(order_way.getText().toString()=="根据创建时间") {
                      order_way.setText("根据修改时间");
                      final_order1 = "modified ";
                      init_list();
                  }

              }
          });

        //升序降序
        up_down=findViewById(R.id.order_pic);
        up_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(final_order2==NotePad.Notes.DEFAULT_SORT_ORDER_UP){
                    final_order2="ASC";
                } else if (final_order2=="ASC") {
                    final_order2=NotePad.Notes.DEFAULT_SORT_ORDER_UP;
                }
                init_list();
            }
        });
        //根据分类查找笔记
        select_type=findViewById(R.id.select_type);
        select_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        //toDo
        //搜索按键
        b1=findViewById(R.id.search_bt);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_order=final_order1+final_order2;
                // 在这里处理点击事件
                String tosearch = editText.getText().toString();
                CustomCursorAdapter adapter1 = null;
                if (tosearch !="") {
                    // 构建查询条件，使用LIKE操作符进行模糊搜索，并结合OR逻辑操作符
                    String selection = NotePad.Notes.COLUMN_NAME_TITLE + " LIKE ? OR " + NotePad.Notes.COLUMN_NAME_NOTE + " LIKE ?";
// 为查询条件提供参数值，注意在搜索词前后添加%以实现任意位置的匹配
                    String[] selectionArgs = new String[]{"%" + tosearch + "%", "%" + tosearch + "%"};
                    //查询得到的数据
                    Cursor cursor = managedQuery(
                            getIntent().getData(),            // Use the default content URI for the provider.
                            PROJECTION,                       // Return the note ID and title for each note.
                            selection,                             // No where clause, return all records.(WHRER 的查询条件)
                            selectionArgs,                             // No where clause, therefore no where column values.(WHRER 的查询参数)
                            //修改了排序
                            final_order  // Use the default sort order.
                    );
                    String[] dataColumns = {NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};
                    int[] viewIDs = {android.R.id.text1, android.R.id.text2};
                    adapter1 = new CustomCursorAdapter(
                            NotesList.this,                             // The Context for the ListView
                            R.layout.noteslist_item,          // Points to the XML for a list item
                            cursor,                           // The cursor to get items from
                            dataColumns,
                            viewIDs
                    );
                    setListAdapter(adapter1);

                } else if (tosearch=="") {
                   init_list();
                }
                editText.setText("");
            }
        });
        //todo
        //添加按钮
        b2 = findViewById(R.id.add_bt);
        //设置事件
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里处理点击事件
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            }
        });
//无条件查询
        init_list();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
        return true;
    }

    //初始化数据(所有记录)
    public void init_list()
    {
        final_order=final_order1+final_order2;
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                this.PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.(WHRER 的查询条件)
                null,                             // No where clause, therefore no where column values.(WHRER 的查询参数)
                final_order  // Use the default sort order.
        );

        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,
                } ;
        int[] viewIDs = { android.R.id.text1,android.R.id.text2};
        CustomCursorAdapter adapter = new CustomCursorAdapter(
                NotesList.this,
                R.layout.noteslist_item,
                cursor,
                dataColumns,
                viewIDs
        );
        setListAdapter(adapter);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        MenuItem mPasteItem = menu.findItem(R.id.menu_paste);
        if (clipboard.hasPrimaryClip()) {
            mPasteItem.setEnabled(true);
        } else {
            mPasteItem.setEnabled(false);
        }
        final boolean haveItems = getListAdapter().getCount() > 0;
        if (haveItems) {
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());
            Intent[] specifics = new Intent[1];
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
            MenuItem[] items = new MenuItem[1];
            Intent intent = new Intent(null, uri);
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
            menu.addIntentOptions(
                Menu.CATEGORY_ALTERNATIVE,  // Add the Intents as options in the alternatives group.
                Menu.NONE,                  // A unique item ID is not required.
                Menu.NONE,                  // The alternatives don't need to be in order.
                null,                       // The caller's name is not excluded from the group.
                specifics,                  // These specific options must appear first.
                intent,                     // These Intent objects map to the options in specifics.
                Menu.NONE,                  // No flags are required.
                items                       // The menu items generated from the specifics-to-
                                            // Intents mapping
            );
                if (items[0] != null) {
                    items[0].setShortcut('1', 'e');
                }
            } else {
                menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
            }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_add){
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        }else if(item.getItemId()== R.id.menu_paste){
            startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            return;
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(), 
                                        Integer.toString((int) info.id) ));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }
        Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
        if(item.getItemId()== R.id.context_open){
            startActivity(new Intent(Intent.ACTION_EDIT, noteUri));
            return true;
        }else if(item.getItemId()== R.id.context_copy){
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newUri(   // new clipboard item holding a URI
                    getContentResolver(),               // resolver to retrieve URI info
                    "Note",                             // label for the clip
                    noteUri)                            // the URI
            );
            return true;
        }else if(item.getItemId()== R.id.context_delete){
            getContentResolver().delete(
                    noteUri,  // The URI of the provider
                    null,     // No where clause is needed, since only a single note ID is being
                    // passed in.
                    null      // No where clause is used, so no where arguments are needed.
            );
            return true;
        }else {
            return super.onContextItemSelected(item);
        }
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
    public void Search(){
        EditText editText=findViewById(R.id.input);
        String et=editText.getText().toString();
        String[] content=new String[]{et};
        Cursor cursor1 = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                "title",                             // No where clause, return all records.(WHRER 的查询条件)
                content,                             // No where clause, therefore no where column values.(WHRER 的查询参数)
                final_order  // Use the default sort order.
        );
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;
        int[] viewIDs = { android.R.id.text1,android.R.id.text2 };
        int[] viewCREATE_DATEs = { android.R.id.text2 };
        SimpleCursorAdapter adapter
                = new SimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor1,                           // The cursor to get items from
                dataColumns,
                viewIDs
        );
        setListAdapter(adapter);
    }

    private void showPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.type_select_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.type_work){
                    selectByType("work");
                } else if (item.getItemId()==R.id.type_study) {
                    selectByType("study");
                }else if (item.getItemId()==R.id.type_life) {
                    selectByType("daylylife");
                }else if (item.getItemId()==R.id.type_privacy) {
                    selectByType("privacy");
                }else if (item.getItemId()==R.id.type_other) {
                    selectByType("other");
                }
                return true;
            }
        });

        popup.show();
    }
  public void selectByType(String type){
      // 构建查询条件，使用LIKE操作符进行模糊搜索，并结合OR逻辑操作符
      String selection = NotePad.Notes.COLUMN_TYPE + " = ? " ;
// 为查询条件提供参数值，注意在搜索词前后添加%以实现任意位置的匹配
      String[] selectionArgs = new String[]{type};
      //查询得到的数据
      Cursor cursor = managedQuery(
              getIntent().getData(),            // Use the default content URI for the provider.
              PROJECTION,                       // Return the note ID and title for each note.
              selection,                             // No where clause, return all records.(WHRER 的查询条件)
              selectionArgs,                             // No where clause, therefore no where column values.(WHRER 的查询参数)
              //修改了排序
              final_order  // Use the default sort order.
      );
      String[] dataColumns = {NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};
      int[] viewIDs = {android.R.id.text1, android.R.id.text2};
      CustomCursorAdapter adapter2 = new CustomCursorAdapter(
              NotesList.this,                             // The Context for the ListView
              R.layout.noteslist_item,          // Points to the XML for a list item
              cursor,                           // The cursor to get items from
              dataColumns,
              viewIDs
      );
      setListAdapter(adapter2);
  }
}
