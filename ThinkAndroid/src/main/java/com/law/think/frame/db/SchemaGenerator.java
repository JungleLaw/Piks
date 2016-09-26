package com.law.think.frame.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.law.think.frame.db.helper.KeywordsAnalysis;
import com.law.think.frame.db.helper.MigrationFileParser;
import com.law.think.frame.db.helper.NumberComparator;
import com.law.think.frame.db.helper.SQLHelper;
import com.law.think.frame.reflect.KernelReflect;
import com.law.think.frame.utils.Logger;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

class SchemaGenerator {

	public static final String THINK = "Think";

	private List<Class<? extends DataSupport>> mClasses;

	private SchemaGenerator(List<Class<? extends DataSupport>> classes) {
		this.mClasses = classes;
	}

	public static SchemaGenerator getInstance(List<Class<? extends DataSupport>> classes) {
		return new SchemaGenerator(classes);
	}

	protected void createDatabase(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		database.beginTransaction();
		try {
			for (Class<? extends DataSupport> dataClass : mClasses) {
				createTable(dataClass, database);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void createTable(Class<? extends DataSupport> table, SQLiteDatabase database) {
		Logger.i("createTable");
		String createSQL = createTableSQL(table);

		if (!createSQL.isEmpty()) {
			try {
				database.execSQL(createSQL);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private String createTableSQL(Class<? extends DataSupport> table) {
		// TODO Auto-generated method stub
		// Field[] fields = table.getDeclaredFields();
		List<Field> fields = KernelReflect.declaredFields(table);
		String tableName = SQLHelper.getTableName(table);
		if (KeywordsAnalysis.isKeyword(tableName)) {
			throw new IllegalArgumentException(tableName + " is SQL Keywords!");
		}

		String createTableSQL = "CREATE TABLE IF NOT EXISTS %s (%s)";
		final ArrayList<String> definitions = new ArrayList<String>();
		definitions.add("id INTEGER PRIMARY KEY AUTOINCREMENT");

		for (Field column : fields) {
			String columnType = SQLHelper.getColumnType(column);
			String columnName = SQLHelper.getColumnName(column);
			if (TextUtils.isEmpty(columnType) || TextUtils.isEmpty(columnName))
				continue;
			StringBuilder definition = new StringBuilder();
			definition.append(columnName).append(" ").append(columnType);
			definitions.add(definition.toString());
		}
		return String.format(createTableSQL, tableName, TextUtils.join(", ", definitions));
	}

	public void doUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		String sql = "select count(*) from sqlite_master where type='table' and name='%s';";

		for (Class<? extends DataSupport> clazz : mClasses) {
			String tableName = SQLHelper.getTableName(clazz);
			Cursor c = sqLiteDatabase.rawQuery(String.format(sql, tableName), null);
			if (c.moveToFirst() && c.getInt(0) == 0) {
				createTable(clazz, sqLiteDatabase);
			} else {
				addColumns(clazz, sqLiteDatabase);
			}
		}
		executeThinkUpgrade(sqLiteDatabase, oldVersion, newVersion);
	}

	private void addColumns(Class<? extends DataSupport> table, SQLiteDatabase sqLiteDatabase) {
		String alterTableSQL = "ALTER TABLE %s ADD COLUMN %s";
		List<Field> fields = KernelReflect.declaredFields(table);
		String tableName = SQLHelper.getTableName(table);
		ArrayList<String> presentColumns = getColumnNames(sqLiteDatabase, tableName);
		ArrayList<String> alterCommands = new ArrayList<>();

		for (Field column : fields) {
			String columnName = SQLHelper.getColumnName(column);
			String columnType = SQLHelper.getColumnType(column);
			if (TextUtils.isEmpty(columnType) || TextUtils.isEmpty(columnName))
				continue;

			if (!presentColumns.contains(columnName)) {
				alterCommands.add(String.format(alterTableSQL, tableName, columnType));
			}
		}

		for (String command : alterCommands) {
			sqLiteDatabase.execSQL(command);
		}
	}

	private boolean executeThinkUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		boolean isSuccess = false;

		try {
			List<String> files = Arrays.asList(ThinkContext.getAssets().list("think_upgrades"));
			Collections.sort(files, new NumberComparator());
			for (String file : files) {

				try {
					int version = Integer.valueOf(file.replace(".sql", ""));

					if ((version > oldVersion) && (version <= newVersion)) {
						executeScript(db, "think_upgrades/", file);
						isSuccess = true;
					}
				} catch (NumberFormatException e) {
					Logger.i(THINK, "not a think script. ignored." + file);
				}

			}
		} catch (IOException e) {
			Logger.e(THINK, e.getMessage());
		}

		return isSuccess;
	}

	protected ArrayList<String> getColumnNames(SQLiteDatabase sqLiteDatabase, String tableName) {
		Cursor resultsQuery = sqLiteDatabase.query(tableName, null, null, null, null, null, null);
		// Check if columns match vs the one on the domain class
		ArrayList<String> columnNames = new ArrayList<>();
		for (int i = 0; i < resultsQuery.getColumnCount(); i++) {
			String columnName = resultsQuery.getColumnName(i);
			columnNames.add(columnName);
		}
		resultsQuery.close();
		return columnNames;
	}

	private void executeScript(SQLiteDatabase db, String path, String file) {
		try {
			InputStream is = ThinkContext.getAssets().open(path + file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			MigrationFileParser migrationFileParser = new MigrationFileParser(sb.toString());
			for (String statement : migrationFileParser.getStatements()) {
				Logger.i(THINK, statement);
				if (!statement.isEmpty()) {
					db.execSQL(statement);
				}
			}

		} catch (IOException e) {
			Logger.e(THINK, e.getMessage());
		}

		Logger.i(THINK, "Script executed");
	}
}