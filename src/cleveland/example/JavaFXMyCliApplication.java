/**
 * Copyright (C) 2015 uphy.jp
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cleveland.example;

import cleveland.console.ConsoleApplication;

import java.io.IOException;


/**
 * JavaFXのコンソールで、CLIアプリケーションを実行するクラスです。
 *
 * @author Yuhi Ishikura
 */
public class JavaFXMyCliApplication extends ConsoleApplication {

  @Override
  protected void invokeMain(final String[] args) {
    MyCliApplication.main(args);
  }



}
