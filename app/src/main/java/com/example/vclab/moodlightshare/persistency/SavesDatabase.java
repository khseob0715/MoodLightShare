package com.example.vclab.moodlightshare.persistency;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.vclab.moodlightshare.model.Save;

@Database(entities = {com.example.vclab.moodlightshare.model.Save.class}, version = 1)
public abstract class SavesDatabase extends RoomDatabase {
    public abstract DAOAccess daoAccess();
}
