package com.example.contentprovider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.example.contentprovider.model.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactManager {
    private Context context;
    private List<Contact> data;

    public ContactManager(Context context) {
        this.context = context;
        getContactData();
        Collections.sort(data);
    }

    private void getContactData() {
        data = new ArrayList<>();
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };

        Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        int nameIndex = phone.getColumnIndex(projection[0]);
        int phoneIndex = phone.getColumnIndex(projection[1]);
        int photoIndex = phone.getColumnIndex(projection[2]);
        phone.moveToFirst();
        while (phone.moveToNext()) {
            String name = phone.getString(nameIndex);
            String number = phone.getString(phoneIndex);
            String photoUri = phone.getString(photoIndex);
            Bitmap photo = getPhotoFromUri(photoUri);
            data.add(new Contact(name, number, photo));

        }
        phone.close();
    }

    private Bitmap getPhotoFromUri(String photoUri) {
        if (photoUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(photoUri));
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public List<Contact> getListContact() {
        return data;
    }
}
