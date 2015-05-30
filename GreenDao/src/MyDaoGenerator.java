import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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


/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Alex
 */
public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.daggerstudio.dao");

        addPwdRec(schema);
        
        File file = new File("./dao-gen");
        if(!file.isDirectory()){
        	file.mkdir();
        }
        new DaoGenerator().generateAll(schema, "./dao-gen");
    }

    private static void addPwdRec(Schema schema) {
        Entity pwdRec = schema.addEntity("Rec");
        pwdRec.addStringProperty("site_url");
        pwdRec.addStringProperty("site_brief").notNull();
        pwdRec.addStringProperty("user_name").notNull();
        pwdRec.addByteArrayProperty("encypted_content").notNull();
        pwdRec.addStringProperty("note");
        pwdRec.addIdProperty().primaryKey().autoincrement();
    }
}
