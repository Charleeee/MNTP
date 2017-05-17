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

package com.example.administrator.mntp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Displays a list of notes. Will display notes from the {@link Uri}
 * provided in the incoming Intent if there is one, otherwise it defaults to displaying the
 * contents of the {@link NotePadProvider}.
 *
 * NOTE: Notice that the provider operations in this Activity are taking place on the UI thread.
 * This is not a good practice. It is only done here to make the code more readable. A real
 * application should use the {@link android.content.AsyncQueryHandler} or
 * {@link android.os.AsyncTask} object to perform operations asynchronously on a separate thread.
 */
public class NotesList extends ListActivity {

    SearchView Note_soso;

    private Button color_button;

    private Button btn_alarmclock;

    private String preferencescolor;

    private SharedPreferences sp;


    private AlarmManager alarmManager;
    private PendingIntent pi;

    // For logging and debugging
    private static final String TAG = "NotesList";

    /**
     * The columns needed by the cursor adapter
     */
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE,// 1
            //加上时间
            NotePad.Notes.COLUMN_NAME_CREATETIME //2

    };

    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 1;

    /**
     * onCreate is called when Android starts this Activity from scratch.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The user does not need to hold down the key to use menu shortcuts.
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

     /*   bindViews();*/

        //显示searchview控件
        setContentView(R.layout.search);
        //获取searchview控件
        Note_soso = (SearchView)findViewById(R.id.soso);

        color_button=(Button)findViewById(R.id.background);

        color_button.setOnClickListener(new ClickEvent());


        if(Note_soso==null){
            return;
        }else {
            //获取ImageView的id
            int imgId = Note_soso.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
            //获取到TextView的ID
            int id = Note_soso.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
            //获取ImageView
            ImageView searchButton = (ImageView) Note_soso.findViewById(imgId);
            //获取到TextView的控件
            TextView textView = (TextView) Note_soso.findViewById(id);
            //设置图片
            searchButton.setImageResource(R.drawable.search);
            //不使用默认
            Note_soso.setIconifiedByDefault(false);
            textView.setTextColor(getResources().getColor(R.color.Black));
        }



        Intent intent = getIntent();

        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }


        getListView().setOnCreateContextMenuListener(this);

        sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        preferencescolor=sp.getString("color", "");
        //设置Listview的背景色
        if(preferencescolor.equals("")){
            getListView().setBackgroundColor(Color.parseColor("#00FFFF"));
        }
        else {
            getListView().setBackgroundColor(Color.parseColor(preferencescolor));
        }


        Cursor cursor = managedQuery(
            getIntent().getData(),            // Use the default content URI for the provider.
            PROJECTION,                       // Return the note ID and title for each note.
            null,                             // No where clause, return all records.
            null,                             // No where clause, therefore no where column values.
            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );



        // The names of the cursor columns to display in the view, initialized to the title column
        final String[] dataColumns = { NotePad.Notes.COLUMN_NAME_CREATETIME, NotePad.Notes.COLUMN_NAME_TITLE } ;

        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = { android.R.id.text2,android.R.id.text1 };

        // Creates the backing adapter for the ListView.
        final SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(
                      this,                             // The Context for the ListView
                      R.layout.noteslist_item,          // Points to the XML for a list item
                      cursor,                           // The cursor to get items from
                      dataColumns,
                      viewIDs
              );

        // Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);

        Note_soso.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){

                    Cursor cursor = managedQuery(
                            getIntent().getData(),            // Use the default content URI for the provider.
                            PROJECTION,                       // Return the note ID and title for each note.
                            NotePad.Notes.COLUMN_NAME_TITLE+ " LIKE '%"+newText+"%' ",                             // 相当于where语句
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
                    //重新setListAdapter
                    setListAdapter(adapter1);
                }else{
                    //恢复默认setListAdapter
                    setListAdapter(adapter);
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);

        // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // The paste menu item is enabled if there is data on the clipboard.
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);


        MenuItem mPasteItem = menu.findItem(R.id.menu_paste);

        // If the clipboard contains an item, enables the Paste option on the menu.
        if (clipboard.hasPrimaryClip()) {
            mPasteItem.setEnabled(true);
        } else {
            // If the clipboard is empty, disables the menu's Paste option.
            mPasteItem.setEnabled(false);
        }

        // Gets the number of notes currently being displayed.
        final boolean haveItems = getListAdapter().getCount() > 0;

        // If there are any notes in the list (which implies that one of
        // them is selected), then we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        if (haveItems) {

            // This is the selected item.
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            // Creates an array of Intents with one element. This will be used to send an Intent
            // based on the selected menu item.
            Intent[] specifics = new Intent[1];

            // Sets the Intent in the array to be an EDIT action on the URI of the selected note.
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);

            // Creates an array of menu items with one element. This will contain the EDIT option.
            MenuItem[] items = new MenuItem[1];

            // Creates an Intent with no specific action, using the URI of the selected note.
            Intent intent = new Intent(null, uri);

            /* Adds the category ALTERNATIVE to the Intent, with the note ID URI as its
             * data. This prepares the Intent as a place to group alternative options in the
             * menu.
             */
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);

            /*
             * Add alternatives to the menu
             */
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
                // If the Edit menu item exists, adds shortcuts for it.
                if (items[0] != null) {

                    // Sets the Edit menu item shortcut to numeric "1", letter "e"
                    items[0].setShortcut('1', 'e');
                }
            } else {
                // If the list is empty, removes any existing alternative actions from the menu
                menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
            }

        // Displays the menu
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:

           startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
           return true;
        case R.id.menu_paste:

          startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
          return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            Log.e(TAG, "bad menuInfo", e);
            return;
        }


        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);

        // If the cursor is empty, then for some reason the adapter can't get the data from the
        // provider, so returns null to the caller.
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);

        // Sets the menu header to be the title of the selected note.
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

        // Append to the
        // menu items for any other activities that can do stuff with it
        // as well.  This does a query on the system for any activities that
        // implement the ALTERNATIVE_ACTION for our data, adding a menu item
        // for each one that is found.
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(), 
                                        Integer.toString((int) info.id) ));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
   /*     menu.setHeaderIcon(R.drawable.to_do);*/
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;


        try {
            // Casts the data object in the item into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {

            // If the object can't be cast, logs an error
            Log.e(TAG, "bad menuInfo", e);

            // Triggers default processing of the menu item.
            return false;
        }
        // Appends the selected note's ID to the URI sent with the incoming Intent.
        Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);

        /*
         * Gets the menu item's ID and compares it to known actions.
         */
        switch (item.getItemId()) {
        case R.id.context_open:
            // Launch activity to view/edit the currently selected item
            startActivity(new Intent(Intent.ACTION_EDIT, noteUri));
            return true;
//BEGIN_INCLUDE(copy)
        case R.id.context_copy:
            // Gets a handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
  
            // Copies the notes URI to the clipboard. In effect, this copies the note itself
            clipboard.setPrimaryClip(ClipData.newUri(   // new clipboard item holding a URI
                    getContentResolver(),               // resolver to retrieve URI info
                    "Note",                             // label for the clip
                    noteUri)                            // the URI
            );
  
            // Returns to the caller and skips further processing.
            return true;
//END_INCLUDE(copy)
        case R.id.context_delete:

            // Deletes the note from the provider by passing in a URI in note ID format.
            // Please see the introductory note about performing provider operations on the
            // UI thread.
            getContentResolver().delete(
                noteUri,  // The URI of the provider
                null,     // No where clause is needed, since only a single note ID is being
                          // passed in.
                null      // No where clause is used, so no where arguments are needed.
            );
  
            // Returns to the caller and skips further processing.
            return true;

          
        default:
            return super.onContextItemSelected(item);
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
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


    class AlarmEvent implements View.OnClickListener {
        @Override
        public void onClick (View v)  {
            Calendar currentTime = Calendar.getInstance();
            new TimePickerDialog(NotesList.this, 0,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view,
                                              int hourOfDay, int minute) {
                            //设置当前时间
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            // 根据用户选择的时间来设置Calendar对象
                            c.set(Calendar.HOUR, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            // Calendar对应的时间启动Activity
                            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                            Log.e("HEHE",c.getTimeInMillis()+"");   //这里的时间是一个unix时间戳
                            // 提示闹钟设置完毕:
                            Toast.makeText(NotesList.this, "闹钟设置完毕~",Toast.LENGTH_SHORT).show();
                        }
                    }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime
                    .get(Calendar.MINUTE), false).show();
        }
    }
}
