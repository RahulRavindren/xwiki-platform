/**
 * ===================================================================
 *
 * Copyright (c) 2003-2005 Ludovic Dubost, All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details, published at
 * http://www.gnu.org/copyleft/gpl.html or in gpl.txt in the
 * root folder of this distribution.
 */
package com.xpn.xwiki.test;

import org.hibernate.HibernateException;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.store.XWikiCacheStore;
import com.xpn.xwiki.store.XWikiCacheStoreInterface;
import com.xpn.xwiki.store.XWikiStoreInterface;

public class StoreHibernateCacheTest extends HibernateTestCase {

    private XWikiStoreInterface store;

    protected void setUp() throws Exception
    {
        super.setUp();

        this.store = new XWikiCacheStore(getXWiki().getStore(), getXWikiContext());
    }
    
    public void testCachedReadWrite() throws XWikiException {
        Utils.setStandardData();
        StoreTest.standardReadWrite(store, Utils.web, Utils.name, getXWikiContext());
        XWikiDocument doc3 = new XWikiDocument(Utils.web, Utils.name);
        doc3 = (XWikiDocument) store.loadXWikiDoc(doc3, getXWikiContext());
        String content3b = doc3.getContent();
        assertEquals(Utils.content3,content3b);
        assertEquals(doc3.getAuthor(), Utils.author2);
        assertEquals(doc3.getVersion(), Utils.version2);
        assertTrue("Document should be from Cache", doc3.isFromCache());
    }

    public void testVirtualCachedReadWrite() throws XWikiException, HibernateException {

        getXWikiContext().setDatabase("xwikitest2");
        cleanUp(getXWiki().getHibernateStore(), getXWikiContext());

        getXWikiContext().setDatabase("xwikitest");
        Utils.setStandardData();
        StoreTest.standardWrite(store, Utils.web, Utils.name, getXWikiContext());
        StoreTest.standardRead(store, Utils.web, Utils.name, getXWikiContext());
        getXWikiContext().setDatabase("xwikitest2");
        Utils.setMediumData();
        StoreTest.standardWrite(store, Utils.web, Utils.name, getXWikiContext());
        StoreTest.standardRead(store, Utils.web, Utils.name, getXWikiContext());
    }

    public void testVirtualCachedReadWrite2() throws XWikiException, HibernateException {

        getXWikiContext().setDatabase("xwikitest2");
        cleanUp(getXWiki().getHibernateStore(), getXWikiContext());

        getXWikiContext().setDatabase("xwikitest");
        Utils.setStandardData();
        StoreTest.standardWrite(store, Utils.web, Utils.name, getXWikiContext());
        getXWikiContext().setDatabase("xwikitest2");
        Utils.setMediumData();
        StoreTest.standardWrite(store, Utils.web, Utils.name, getXWikiContext());

        ((XWikiCacheStoreInterface)store).flushCache();
        Utils.setStandardData();
        getXWikiContext().setDatabase("xwikitest");
        StoreTest.standardRead(store, Utils.web, Utils.name, getXWikiContext());
        Utils.setMediumData();
        getXWikiContext().setDatabase("xwikitest2");
        StoreTest.standardRead(store, Utils.web, Utils.name, getXWikiContext());
    }

}
