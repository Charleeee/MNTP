# 期中作业NotePad
其中大多数基本功能不变，新添加除了基本两个功能搜索和时间戳外，还增加了一个更改背景颜色功能

## 基本功能1：时间戳

### 1、首先进入布局文件notelist_item.xml中添加显示时间的TextView
```java
<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="#FFFFE0">
        <TextView xmlns:android="http://schemas.android.com/apk/res/android"
            android:id="@android:id/text1"
            android:layout_marginLeft="20dp"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="?android:attr/textAppearanceLarge"
            android:gravity="center_vertical"
            android:paddingLeft="5dip"
            android:textColor="#000000"
            android:textSize="22dp"
            android:singleLine="true"
            />
        <TextView xmlns:android="http://schemas.android.com/apk/res/android"
            android:id="@android:id/text2"
            android:layout_marginLeft="20dp"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="?android:attr/textAppearanceLarge"
            android:gravity="center_vertical"
            android:paddingLeft="5dip"
            android:textColor="#708090"
            android:textSize="15dp"
            android:singleLine="true"
            />
            
```

### 2、添加显示时间格式的类DTStyle

```java
public class DTStyle
{
    private static String defaultDateStyle = "yyyy-MM-dd";

    private static String defaultTimeStyle = "yyyy-MM-dd HH:mm:ss";


    public static String getPresentTime()
    {
        Date today = new Date();
        return convertDateToString(today,defaultDateStyle);
    }

    public static String getPresentTimeString()
    {
        Date today = new Date();
        return convertDateToString(today, defaultTimeStyle);
    }

    public static String convertDateToString(Date date)
    {
        return convertDateToString(date, defaultDateStyle);
    }

    public static String convertDateToString(Date date, String pattern)
    {
        String returnValue = "";

        if (date != null)
        {
            SimpleDateFormat ds = new SimpleDateFormat(pattern);
            returnValue = ds.format(date);
        }
        return returnValue;
    }
}
```

### 3、进入NoteList.java中添加时间戳相关值

```java
private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, 
            NotePad.Notes.COLUMN_NAME_TITLE,
        
            NotePad.Notes.COLUMN_NAME_CREATE_DATE //创建时间

    };

        // The names of the cursor columns to display in the view, initialized to the title column
        final String[] dataColumns = { NotePad.Notes.COLUMN_NAME_CREATETIME, NotePad.Notes.COLUMN_NAME_TITLE } ;

        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = { android.R.id.text2,android.R.id.text1 };
```
## 时间戳显示截图：

![image](https://github.com/Charleeee/MNTP/blob/master/image/main.png)

## 基本功能2：搜索

### 1、首先进入布局文件中添加搜索的SearchView

```java
<SearchView
            android:id="@+id/soso"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:iconifiedByDefault="false" />
```

### 2、进入NoteList.java中添加搜索变量以及相关函数

```java
SearchView Note_soso;

 setContentView(R.layout.search);
 
 Note_soso = (SearchView)findViewById(R.id.soso);
 
 if(Note_soso==null){
            return;
        }else {
            int imgId = Note_soso.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
            int id = Note_soso.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
            ImageView searchButton = (ImageView) Note_soso.findViewById(imgId);
            TextView textView = (TextView) Note_soso.findViewById(id);
            searchButton.setImageResource(R.drawable.search);
            Note_soso.setIconifiedByDefault(false);
            textView.setTextColor(getResources().getColor(R.color.Black));
        }
        
        Note_soso.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){

                    Cursor cursor = managedQuery(
                            getIntent().getData(),            // Use the default content URI for the provider.
                            PROJECTION,                       // Return the note ID and title for each note.
                            NotePad.Notes.COLUMN_NAME_TITLE+ " LIKE '%"+newText+"%' ",                            
                            null,                             // No where clause, therefore no where column values.
                            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
                    );
                    final String[] dataColumn = { NotePad.Notes.COLUMN_NAME_CREATETIME, NotePad.Notes.COLUMN_NAME_TITLE } ;

                    // The view IDs that will display the cursor columns, initialized to the TextView in
                    // noteslist_item.xml
                    int[] viewID = {android.R.id.text2,android.R.id.text1 };
                    SimpleCursorAdapter adapter1
                            = new SimpleCursorAdapter(
                            NotesList.this,                             // The Context for the ListView
                            R.layout.noteslist_item,          // Points to the XML for a list item
                            cursor,                           // The cursor to get items from
                            dataColumn,
                            viewID
                    );
                    setListAdapter(adapter1);
                }else{
                    setListAdapter(adapter);
                }
                return false;
            }
        }
```
## 搜索截图：

![image](https://github.com/Charleeee/MNTP/blob/master/image/search1.png)
![image](https://github.com/Charleeee/MNTP/blob/master/image/search2.png)

## 附加功能：更改主界面背景颜色

### 1、首先在布局文件中添加select_color.xml，以便于点击按钮时选择颜色

```java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="40dp"
        android:orientation="horizontal"
        android:gravity="center"
        android:background="@color/Black">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="设置NotePad背景颜色"
            android:textSize="20dp"
            android:textColor="@color/White"/>
    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:orientation="horizontal"
        android:clickable="true">
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#FF0000"
            android:id="@+id/Red"
            android:clickable="true"
            android:layout_weight="1"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#FFA500"
            android:id="@+id/Orange"
            android:layout_weight="1"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#FFFF00"
            android:id="@+id/Yellow"
            android:layout_weight="1"/>
        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#00FFFF"
            android:id="@+id/Green"
            android:layout_weight="1"/>
		<TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#228B22"
            android:id="@+id/Blue"
            android:layout_weight="1"/>
		<TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#000000"
            android:id="@+id/Black"
            android:layout_weight="1"/>
		<TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#800080"
            android:id="@+id/Purple"
            android:layout_weight="1"/>
    </LinearLayout>
</LinearLayout>
```
### 2、设置变量，以及点击按钮时发生的点击事件
```java
    private Button btn_color;

    btn_color=(Button)findViewById(R.id.background);
    btn_color.setOnClickListener(new ClickEvent());
    
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick (View v)  {
        }
    }
```
### 3、设置选择颜色时点击事件监听器
```java
class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick (View v)  {
            final AlertDialog alertDialog = new AlertDialog.Builder(NotesList.this).create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.select_color);

            TextView color_Red = (TextView)alertDialog.getWindow().findViewById(R.id.Red);
            TextView color_Orange = (TextView)alertDialog.getWindow().findViewById(R.id.Orange);
            TextView color_Yellow = (TextView)alertDialog.getWindow().findViewById(R.id.Yellow);
            TextView color_Green = (TextView)alertDialog.getWindow().findViewById(R.id.Green);
			TextView color_Blue = (TextView)alertDialog.getWindow().findViewById(R.id.Blue);
			TextView color_Black = (TextView)alertDialog.getWindow().findViewById(R.id.Black);
			TextView color_Purple = (TextView)alertDialog.getWindow().findViewById(R.id.Purple);

            color_Red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#FF0000";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });

            color_Orange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#FFA500";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });

            color_Yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#FFFF00";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });

            color_Green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#00FFFF";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });
			color_Blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#228B22";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });
			color_Black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#000000";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });
			color_Purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencescolor="#800080";
                    getListView().setBackgroundColor(Color.parseColor(preferencescolor));
                    putColor(preferencescolor);
                    alertDialog.dismiss();
                }
            });

        }
    }
    private void putColor(String color){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("color", color);
        editor.commit();
    }
```
## 更改背景颜色截图（随机选择其中两种颜色）：
![image](https://github.com/Charleeee/MNTP/blob/master/image/changecolor.png)
![image](https://github.com/Charleeee/MNTP/blob/master/image/yellow.png)
![image](https://github.com/Charleeee/MNTP/blob/master/image/blue.png)

## 其他功能基本保持不变

### 1、编辑文本界面
![image](https://github.com/Charleeee/MNTP/blob/master/image/bianji.png)
### 2、编辑标题界面
![image](https://github.com/Charleeee/MNTP/blob/master/image/title.png)
### 3、长按文本
![image](https://github.com/Charleeee/MNTP/blob/master/image/list.png)
