package com.japho.ueab.root;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import Tasks.DownloadImageTask;

public class Helper {
    public static boolean isNullOrEmpty(String s){ return s == null || s.trim() == ""; }
    public static boolean isNullOrEmpty(Object[] o) { return o == null || o.length < 1; }

    public static void setImage(View view, String URL, ImageView imageView){
        if (view != null) {
            Glide.with(view)
                    .load(URL)
                    .into(imageView);
        }
        else {
            if (URL != null)
                new DownloadImageTask(imageView).execute(URL);
        }
    }
    public static void setImage(Activity activity, String URL, ImageView view){
        if (activity != null) {
            Glide.with(activity)
                    .load(URL)
                    .into(view);
        }
        else {
            if (URL != null)
                new DownloadImageTask(view).execute(URL);
        }
    }

    public static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

   /* public static String[] getCategoryArrayFromSnapshot(DataSnapshot dataSnapshot, String firstOption){
        List<Object> subCategoriesList = (ArrayList<Object>) dataSnapshot.getValue();

        Map<Integer, String> categories = new HashMap<>();
        for ( Object category : subCategoriesList ) {
            int index = subCategoriesList.indexOf(category);
            //itemMap is a single item, but still in json format.
            //From this object, extract wanted data to item, and add it to our list of items.
            if(category instanceof Map){
                Map<String, Object> categoryObj = (Map<String, Object>) category;

                String name = (String) categoryObj.get("Name");
                categories.put( index, name);
            }
        }

        String[] options;
        options = new String[categories.size() + 1];
        options[0] = firstOption;

        for(Integer key : categories.keySet()){
            String name = categories.get(key);

            options[key+1] = name;
        }

        return options;
    }

    */
   public static String[] getCategoryArrayFromSnapshot(String choose){

       String[] options;
       options = new String[9];
       options[0] = "Click to select category";
       options[1] = "Home appliances";
       options[2] = "Electronics and accessories";
       options[3] = "Ebooks";
       options[4] = "Clothing";
       options[5] = "Softwares";
       options[6] = "Services";
       options[7] = "Food";
       options[8] = "Others";
       return options;

}
}

