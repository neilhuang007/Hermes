package dev.hermes.ui.alt.account;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.hermes.Hermes;
import dev.hermes.utils.file.FileType;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AltSaving extends dev.hermes.utils.file.File {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    public AltSaving(final File file, final FileType fileType) {
        super(file, fileType);
    }

    @Override
    public boolean read() {
        if (!this.getFile().exists()) {
            return false;
        }

        Hermes.accountManager.getAccounts().clear();

        try {
            // reads file to a json object
            final FileReader fileReader = new FileReader(this.getFile());
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final JsonObject jsonObject = GSON.fromJson(bufferedReader, JsonObject.class);

            // closes both readers
            bufferedReader.close();
            fileReader.close();

            // checks if there was data read
            if (jsonObject == null) {
                return false;
            }
            
            for (Map.Entry<String, JsonElement> jsonElement : jsonObject.entrySet()) {
                if (jsonElement.getKey().equals("Metadata")) {
                    continue;
                }

                // TODO: Might wanna add "has" checks for each field so it doesn't shit itself while loading
                JsonObject accountJSONElement = jsonElement.getValue().getAsJsonObject();
                String password = "";
                String refreshToken = "";
                if(accountJSONElement.has("password")){
                    password = accountJSONElement.get("password").getAsString();
                }
                if(accountJSONElement.has("refreshtoken")){
                    refreshToken = accountJSONElement.get("refreshtoken").getAsString();
                }
                String username = accountJSONElement.get("username").getAsString();
                String uuid = accountJSONElement.get("uuid").getAsString();
                String accounttype = accountJSONElement.get("accounttype").getAsString();
                Account account = new Account(username,password, uuid, refreshToken,accounttype);
                Hermes.accountManager.getAccounts().add(account);
            }

        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }

    @Override
    public boolean write() {
        try {
            // creates the file
            if (!this.getFile().exists()) {
                this.getFile().createNewFile();
            }

            // creates a new json object where all data is stored in
            final JsonObject jsonObject = new JsonObject();

            // Add some extra information to the config
            final JsonObject metadataJsonObject = new JsonObject();
            metadataJsonObject.addProperty("version", Hermes.VERSION);
            metadataJsonObject.addProperty("creationDate", DATE_FORMATTER.format(new Date()));
            jsonObject.add("Metadata", metadataJsonObject);

            for (Account account : Hermes.accountManager.getAccounts()) {
                if (account.getUsername() == null) {
                    continue;
                }

                final JsonObject moduleJsonObject = new JsonObject();
                moduleJsonObject.addProperty("username", account.getUsername());
                moduleJsonObject.addProperty("password", account.getPassword());
                moduleJsonObject.addProperty("uuid", account.getUuid());
                moduleJsonObject.addProperty("refreshtoken", account.getRefreshToken());
                moduleJsonObject.addProperty("accounttype", account.getAccountType());
                jsonObject.add(account.getUsername(), moduleJsonObject);
            }

            // writes json object data to a file
            final FileWriter fileWriter = new FileWriter(getFile());
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            GSON.toJson(jsonObject, bufferedWriter);

            // closes the writer
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.flush();
            fileWriter.close();
        } catch (final IOException ignored) {
            return false;
        }

        return true;
    }
}
