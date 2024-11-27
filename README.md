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
   ```SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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
``` String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,
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

  ```b1.setOnClickListener(new View.OnClickListener() {
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
  实现效果：在主界面添加了“笔记类型按钮”
          点击显示菜单，可选择查询的类型
          示例：点击工作类型后，显示所有类型为“工作”的笔记
          
**2.ui美化**
  实现效果：修改了主页面的主体风格
            为每条笔记根据类型添加了图片
            在主页面顶部添加了软件图标，搜索框，搜索按钮
            添加了排序按钮和分类选择按钮
            主页面右下方添加了“新增笔记”按钮

**3.排序管理**
  实现效果：点击“升序降序”按钮可切换笔记的升序降序排列
                点击“排列方式”按钮可依次切换笔记的排列方式（排列方式默认为根据修改时间，然后是根据标题，根据创建时间）
                
**4.文件储存**
  实现效果：在笔记编辑界面点出右上角的菜单，选择“导出笔记”选项，把当前笔记按一定格式存储到默认的文件存储路径中
