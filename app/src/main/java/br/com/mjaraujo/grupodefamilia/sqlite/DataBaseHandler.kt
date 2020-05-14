package br.com.mjaraujo.grupodefamilia.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import br.com.mjaraujo.grupodefamilia.model.Pessoa

val DATABASENAME = "dbGpFamilia"
val TABLENAME = "Pessoas"
val COL_NOME = "nome"
val COL_ID = "id"
val COL_CPF = "cpf"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(
    context, DATABASENAME, null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE " + TABLENAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_NOME + " VARCHAR(256)," + COL_CPF + " VARCHAR(256))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData(pessoa: Pessoa) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NOME, pessoa.nome)
        contentValues.put(COL_CPF, pessoa.cpf)
        val result = database.insert(TABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Falha ao registrar usuário", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Usuário registrado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    fun readData(): MutableList<Pessoa> {
        val list: MutableList<Pessoa> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val user = Pessoa()
                user.id = result.getInt(result.getColumnIndex(COL_ID)).toString()
                user.nome = result.getString(result.getColumnIndex(COL_NOME))
                user.cpf = result.getString(result.getColumnIndex(COL_CPF)).toString()
                list.add(user)
            } while (result.moveToNext())
        }
        return list
    }
}