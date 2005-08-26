/* ====================================================================
 *   Copyright 2005 J�r�mi Joslin.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

package com.xpn.xwiki.plugin.packaging;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

public class PackagePlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {

        public PackagePlugin(String name, String className, XWikiContext context) {
            super(name, className, context);
            init(context);
        }

    public String getName() {
        return "package";
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        try {
            return new PackageAPI(new Package(), context);
        } catch (PackageException e) {
            return null;
        }
    }

    public void flushCache() {
    }

    public void init(XWikiContext context) {
        super.init(context);
    }
}
