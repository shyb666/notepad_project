# notepad_project
期中作业：notepad
# 基本功能
**1.时间戳**
  实现效果：为每条笔记添加了时间戳（根据修改时间）
  ![图片描述](https://github.com/shyb666/pictures/blob/main/AG2OILXEMYLZLC5%5B%5DIOFWB1.png)

  实现方法：
  由于该项目的主页与编辑页面原本共用一个布局的设计不利于功能的修改，因此**重新创建了一个主页的布局**(如上图)
  
  该项目已经提供了修改时间(modifyed),但是格式为毫秒数，且不显示
  
  所以必须修改事件的显示格式，
  
  首先在**NotePadProvider类的insert方法中**,修改**新增笔记时设置的时间的格式**
   ```
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date=new Date(System.currentTimeMillis());
        String created_date=sdf.format(date);
          //修改创建时间
        // If the values map doesn't contain the creation date, sets the value to the current time.
        if (values.containsKey(NotePad.Notes.COLUMN_NAME_CREATE_DATE) == false) {
            values.put(NotePad.Notes.COLUMN_NAME_CREATE_DATE, created_date);
        }
       //把上次修改时间设为创建时间
        if (values.containsKey(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE) == false) {
            values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, created_date);
        }
```

  然后在**NoteEditor类的updateNote方法中**,修改**新增笔记时设置的时间的格式**

 ```
 ContentValues values = new ContentValues();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date=new Date(System.currentTimeMillis());
        String modification_date=sdf.format(date);
        values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, modification_date);
 ```
  实现了修改内容的同时修改表中的modifyed列
  
  **NoteList类**中修改主页查询时获取的数据类型，**增加modifyed属性，绑定到布局的时间戳组件，并重新设置适配器**
```
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
```
  其中**CustomCursorAdapter继承自原项目的SimpleCursorAdapter**,用于重新设置适配器格式，实现ListView**图片的显示**
  与该部分无关
  
**2.搜索功能**
  实现效果：在搜索框输入内容或标题(此处输入no作为搜索内容)，
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/2JPN_4T2TFPRH6NN0TVPMMH.png)
  
  点击搜索按钮，可查询到具有相应内容或标题的笔记条目，
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p2.png)
  
  然后清空搜索框内容
  
  若搜索框无内容，此时点击搜索按钮，则查询所有笔记
  
  实现方法：
  
  首先在新的主页布局文件中添加EditText和Button等必要组件，并进行绑定
  
  然后为搜索按键添加点击事件，点击时根据搜索框内容重新查询数据，并设置适配器

  ```
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
 ```

其中**init_list()**;方法封装了原本项目在查询所有条目并设置适配器的方法，即当搜索框为空时，查询所有条目

把原本的排列顺序属性拆分为“升降序”(final_order1)和“排列属性”(final_order2)，并重新组成新的排列顺序**final_order**，便于修改排序方式


# 拓展功能
**1.分类管理**
  实现效果：为每条笔记添加了类型属性，可在修改标题界面中设置类型
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p5.png)
  
  每个类型有对应的图片，显示在主页ListView的图片上(见主页图)
  
  在主界面添加了“笔记类型按钮”，点击后显示菜单，可选择查询的类型
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p6.png) 
  
  示例：点击工作类型后，显示所有类型为“工作”的笔记
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p7.png)   
  
  实现方法：
  首先在数据库更新时新增**type**列，更改数据库版本
  ```
@Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           // Logs that the database is being upgraded
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");
           //添加类

           db.execSQL("ALTER TABLE notes ADD COLUMN type TEXT");
           // Kills the table and existing data
           db.execSQL("DROP TABLE IF EXISTS notes");

           // Recreates the database with a new version
           onCreate(db);
       }
```

  在新添加一个适配器**CustomCursorAdapter**(继承自原项目的SimpleCursorAdapter)，用于显示图片  
重写其中重要方法实现图片显示

```
@Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 调用super.bindView()来绑定基本的数据
        super.bindView(view, context, cursor);

        // 获取ImageView
        ImageView imageView = view.findViewById(R.id.note_type);

        // 获取用于决定图片的条件列的值
        int columnIndex = cursor.getColumnIndexOrThrow(NotePad.Notes.COLUMN_TYPE);
        String value = cursor.getString(columnIndex); // 假设这一列是整型，根据实际情况调整
if(value==null){
    value="work";
}
        // 使用switch语句来设置不同的图片
        switch (value) {
            case "work":
                imageView.setImageResource(R.drawable.work);
                break;
            case "study":
                imageView.setImageResource(R.drawable.study);
                break;
            case "other":
                imageView.setImageResource(R.drawable.other);
                break;
            case "daylylife":
                imageView.setImageResource(R.drawable.life);
                break;
            case "privacy":
                imageView.setImageResource(R.drawable.privacy);
                break;
            // 添加更多的case来处理其他值
            default:
                imageView.setImageResource(R.drawable.item_background);
                break;
        }
    }
```
查询数据时也要新增对**type**属性的查询

修改标题编辑界面，新增各类型的按键，点击时设置本条笔记类型
在**TitleEditor类**中，先用一个变量**selected_type**接收点击不同按钮对应的类型然后在关闭界面时**设置类型**
```
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
```
让主页的“类型查询”按键点击时显示菜单，为对应菜单项添加对应的类型查询(selectByType实现方法类似于搜索功能的实现，不再重复)
```
select_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
  ```
```
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
```

**2.排序管理**
  实现效果：点击“升序降序”按钮可切换笔记的升序降序排列
  切换前
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p8.png)
  
  切换后
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p9.png)
  
  点击“排列方式”按钮可依次切换笔记的排列方式（排列方式默认为根据修改时间，然后是根据标题，根据创建时间）
  
  切换后排列方式为标题后(仍可以修改升降序)
  
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p10.png)
  
  实现方式： 把项目原本的排列顺序属性拆分为“升降序”(final_order1)和“排列属性”(final_order2)
  
  点击升降序按钮时修改final_order1属性，点击排序方式按钮时修改final_order2属性

  每次查询之前，把**final_order1**和**final_order2**属性合并为**final_order**属性用于排序，代码相对简单
               
**3.文件储存**
  实现效果：在笔记编辑界面点出右上角的菜单，选择“导出笔记”选项，把当前笔记按一定格式存储到默认的文件存储路径中
  
   ![图片描述](https://github.com/shyb666/pictures/blob/main/p4.png)
.
    ![图片描述](https://github.com/shyb666/pictures/blob/main/p11.png)
    
  首先添加对应菜单文件的菜单项，点击后执行**exportNote()**方法，设置固定格式，实现文件导出，
  
  ```private final void exportNote() {
        if (mCursor != null && mCursor.moveToFirst()) {
            // 获取笔记数据
            int id=mCursor.getInt(mCursor.getColumnIndexOrThrow("_id"));
            String title = mCursor.getString(mCursor.getColumnIndexOrThrow("title"));
            String note = mCursor.getString(mCursor.getColumnIndexOrThrow("note"));
            String updateTime = mCursor.getString(mCursor.getColumnIndexOrThrow("modified"));
            // 准备导出的文本内容
            String content = "Title: " + title + "\n" +
                    "ID: " + id + "\n" +
                    "Note: " + note + "\n" +
                    "Updated: " + updateTime + "\n";

            // 导出为文本文件
            File file = new File(this.getExternalFilesDir(null), "note_" + title + ".txt");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
                Toast.makeText(this, "Succeed to export note"+ id+"||"
                        +file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to export note", Toast.LENGTH_SHORT).show();
                if (mCursor != null) mCursor.close();
                return;
            }

        } else {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
            if (mCursor != null) mCursor.close();
        }

    }
```
  
**4.ui美化**
  实现效果：修改了主页面的主体风格
  
            为每条笔记根据类型添加了图片
            
            在主页面顶部添加了软件图标，搜索框，搜索按钮
            
            添加了排序按钮和分类选择按钮
            
            主页面右下方添加了“新增笔记”按钮
            
  
