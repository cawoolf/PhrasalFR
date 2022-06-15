package com.example.phrasalfr.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Phrase::class], version = 1)
abstract class PhraseDatabase : RoomDatabase() {

    abstract val phraseDao: PhraseDao

    companion object {
        @Volatile
        private var INSTANCE: PhraseDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PhraseDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhraseDatabase::class.java,
                    "phrase_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(PhraseDatabaseCallBack(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }


        private class PhraseDatabaseCallBack(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.phraseDao)
                    }
                }
            }
        }

        suspend fun populateDatabase(phraseDao: PhraseDao) {

            var phrase1 = Phrase("Greetings","Hey","Salut")
            phraseDao.insertPhrase(phrase1)

            var phrase2 = Phrase("Verbs","I go","Je vais")
            phraseDao.insertPhrase(phrase2)

            var phrase3 = Phrase("Verbs","I want","Je veux")
            phraseDao.insertPhrase(phrase3)

            var phrase4 = Phrase("Verbs","You want","Tu veux")
            phraseDao.insertPhrase(phrase4)

        }

    }

}