# notepad_project
期中作业：notepad
# 基本功能
1.时间戳
  实现效果：为每条笔记添加了时间戳（根据修改时间）
  ![图片描述](https://github.com/shyb666/pictures/blob/main/AG2OILXEMYLZLC5%5B%5DIOFWB1.png)
  实现方法：
  ```bash
git clone https://github.com/your-username/your-project.git
   cd your-project

  
2.搜索功能
  实现效果：在搜索框输入内容或标题(此处输入no作为搜索内容)，
  ![图片描述](https://github.com/shyb666/pictures/blob/main/2JPN_4T2TFPRH6NN0TVPMMH.png)
  点击搜索按钮，可查询到具有相应内容或标题的笔记条目，
  ![图片描述](https://github.com/shyb666/pictures/blob/main/p2.png)
  然后清空搜索框内容
  若搜索框无内容，此时点击搜索按钮，则查询所有笔记
  
# 拓展功能
1.分类管理
  实现效果：在主界面添加了“笔记类型按钮”
          点击显示菜单，可选择查询的类型
          示例：点击工作类型后，显示所有类型为“工作”的笔记
          
2.ui美化
  实现效果：修改了主页面的主体风格
            为每条笔记根据类型添加了图片
            在主页面顶部添加了软件图标，搜索框，搜索按钮
            添加了排序按钮和分类选择按钮
            主页面右下方添加了“新增笔记”按钮

3.排序管理
  实现效果：点击“升序降序”按钮可切换笔记的升序降序排列
                点击“排列方式”按钮可依次切换笔记的排列方式（排列方式默认为根据修改时间，然后是根据标题，根据创建时间）
                
4.文件储存
  实现效果：在笔记编辑界面点出右上角的菜单，选择“导出笔记”选项，把当前笔记按一定格式存储到默认的文件存储路径中
