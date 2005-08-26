/**
 * ===================================================================
 *
 * Copyright (c) 2003 Ludovic Dubost, All rights reserved.
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
 *
 * User: ludovic
 * Date: 13 mars 2004
 * Time: 15:02:45
 */

package com.xpn.xwiki.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.cactus.WebRequest;
import org.apache.cactus.WebResponse;
import org.apache.cactus.client.authentication.Authentication;
import org.apache.cactus.client.authentication.BasicAuthentication;
import org.hibernate.HibernateException;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.store.XWikiHibernateStore;
import com.xpn.xwiki.test.smtp.SimpleSmtpServer;
import com.xpn.xwiki.test.smtp.SmtpMessage;
import com.xpn.xwiki.user.api.XWikiRightService;


public class ServletAuthTest extends ServletTest {
    protected SimpleSmtpServer server = null;


    public void startSmtpServer() {
        if ((server!=null)&&(server.isStopped()==false)) {
            try {
                server.stop();
            } catch (Exception e) {}
        }
        server = SimpleSmtpServer.start();
    }

    public void stopSmtpServer() {
        try {
           if (server.isStopped()==false);
               server.stop();
        } catch (Exception e) {}
    }

    public SmtpMessage getLastMessage() {
     SmtpMessage email = null;
     Iterator emailIter = server.getReceivedEmail();
     while (emailIter.hasNext()) {
        email = (SmtpMessage)emailIter.next();
     }
     return email;
    }

    public void updateRight(String fullname, String user, String group, String level, boolean allow, boolean global) throws XWikiException {
        Utils.updateRight(xwiki, context, fullname, user, group, level, allow, global);
    }

    public void beginAuthNeeded(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        context.setDatabase("xwikitest");
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "AuthNeededTest", context);
        updateRight("Main.AuthNeededTest", "XWiki.LudovicDubost", "", "view", true, false);
        setUrl(webRequest, "view", "AuthNeededTest");
    }

    public void endAuthNeeded(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 302", 302, webResponse.getStatusCode());
            assertTrue("There should be a redirect to login page", webResponse.getText().indexOf("login")==-1);
        } finally {
            clientTearDown();
        }

    }

    public void testAuthNeeded() throws Throwable {
        launchTest();
    }

    public void beginAuthNeededAuthOnView(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        context.setDatabase("xwikitest");
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "AuthNeededTest", context);
        Utils.createDoc(hibstore, "XWiki", "XWikiPreferences", context);
        Utils.setIntValue("XWiki.XWikiPreferences", "authenticate_view", 1, context);
        setUrl(webRequest, "view", "AuthNeededTest");
    }

    public void endAuthNeededAuthOnView(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 302", 302, webResponse.getStatusCode());
            assertTrue("There should be a redirect to login page", webResponse.getText().indexOf("login")==-1);
        } finally {
            clientTearDown();
        }

    }

    public void testAuthNeededAuthOnView() throws Throwable {
        launchTest();
    }


    public void beginAuthPage(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        context.setDatabase("xwikitest");
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        setUrl(webRequest, "login", "XWiki", "XWikiLogin", "");
    }

    public void endAuthPage(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 401", 401, webResponse.getStatusCode());
            assertTrue("Content should be login page", webResponse.getText().indexOf("j_username")!=-1);
        } finally {
            clientTearDown();
        }

    }

    public void testAuthPage() throws Throwable {
        launchTest();
    }


    public void beginAuthPage2(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        context.setDatabase("xwikitest");
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        updateRight("XWiki.XWikiPreferences", "XWiki.LudovicDubost","","edit", true, true);
        setUrl(webRequest, "login", "XWiki", "XWikiLogin", "");
    }

    public void endAuthPage2(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 401", 401, webResponse.getStatusCode());
            assertTrue("Content should be login page", webResponse.getText().indexOf("j_username")!=-1);
        } finally {
            clientTearDown();
        }

    }

    public void testAuthPage2() throws Throwable {
        launchTest();
    }


    public void beginAuthPage3(WebRequest webRequest) throws HibernateException, XWikiException {
          XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
          context.setDatabase("xwikitest");
          StoreHibernateTest.cleanUp(hibstore, context);
          clientSetUp(hibstore);
          updateRight("XWiki.XWikiPreferences", "XWiki.LudovicDubost","","edit, view", true, true);
          setUrl(webRequest, "login", "XWiki", "XWikiLogin", "");
      }

      public void endAuthPage3(WebResponse webResponse) throws HibernateException {
          try {
              assertEquals("Response status should be 401", 401, webResponse.getStatusCode());
              assertTrue("Content should be login page", webResponse.getText().indexOf("j_username")!=-1);
          } finally {
              clientTearDown();
          }

      }

      public void testAuthPage3() throws Throwable {
          launchTest();
      }

    public void beginBasicAuth(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "WebHome", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("LudovicDubost", map, "", "", "view, edit", context);
        updateRight("Main.WebHome", "XWiki.LudovicDubost", "", "view", true, false);
        setUrl(webRequest, "view", "Main", "WebHome", "");
        Authentication auth = new BasicAuthentication("LudovicDubost", "toto");
        webRequest.setAuthentication(auth);
    }

    public void endBasicAuth(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            String result = webResponse.getText();
            assertTrue("Could not find WebHome Content: " + result, result.indexOf("Hello 1")!=-1);
        } finally {
            clientTearDown();
        }

    }


   public void testBasicAuth() throws Throwable {
    launchTest();
   }



    public void beginFormAuth(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "WebHome", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("LudovicDubost", map, "", "", "view, edit", context);
        updateRight("Main.WebHome", "XWiki.LudovicDubost", "", "view", true, false);
        setUrl(webRequest, "view", "WebHome");

        MyFormAuthentication auth = new MyFormAuthentication("LudovicDubost", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);
    }

    public void endFormAuth(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            String result = webResponse.getText();
            assertTrue("Could not find WebHome Content: " + result, result.indexOf("Hello 1")!=-1);
        } finally {
            clientTearDown();
        }

    }

    public void testFormAuth() throws Throwable {
        launchTest();
    }


    public void beginFormAuthCaseInsensitive(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "WebHome", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("LudovicDubost", map, "", "", "view, edit", context);
        updateRight("Main.WebHome", "XWiki.LudovicDubost", "", "view", true, false);
        setUrl(webRequest, "view", "WebHome");

        MyFormAuthentication auth = new MyFormAuthentication("ludovicdubost", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);
    }

    public void endFormAuthCaseInsensitive(WebResponse webResponse) throws HibernateException {
        try {
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            String result = webResponse.getText();
            assertTrue("Could not find WebHome Content: " + result, result.indexOf("Hello 1")!=-1);
        } finally {
            clientTearDown();
        }

    }

    public void testFormAuthCaseInsensitive() throws Throwable {
        launchTest();
    }

    public void beginFormAuthWithEmptyPass(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "WebHome", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("LudovicDubost", map, "", "", "view, edit", context);
        updateRight("Main.WebHome", "XWiki.LudovicDubost", "", "view", true, false);
        setUrl(webRequest, "view", "WebHome");

        // Can't seem to be able to test failed auth
        // MyFormAuthentication auth = new MyFormAuthentication("PierreDupont", "");
        //auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        // webRequest.setAuthentication(auth);
    }

    public void endFormAuthWithEmptyPass(WebResponse webResponse) throws HibernateException {
        try {
        }
        finally {
            clientTearDown();
        }
    }


    public void testFormAuthWithEmptyPass() throws Throwable {
        try {
            launchTest();
        } catch (Exception e) {}
    }

    public void beginFormAuthWithWrongPass(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        Utils.createDoc(hibstore, "Main", "WebHome", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("LudovicDubost", map, "", "", "view, edit", context);
        updateRight("Main.WebHome", "XWiki.LudovicDubost", "", "view", true, false);
        setUrl(webRequest, "view", "WebHome");

        // Cannot test this.. too bad..
        //MyFormAuthentication auth = new MyFormAuthentication("LudovicDubost", "tata");
        //auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        //webRequest.setAuthentication(auth);
    }

    public void endFormAuthWithWrongPass(WebResponse webResponse) throws HibernateException {
        try {
        }
        finally {
            clientTearDown();
        }
    }


    public void testFormAuthWithWrongPass() throws Throwable {
        try {
        launchTest();
        } catch (Exception e) {}
    }



    public void beginCreateUserNoRight(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);

        prepareEmailPreferences("CreateUserTest", "$xwiki.createUser()", hibstore);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("Admin", map, "", "", "view, edit", context);

        setUrl(webRequest, "view", "CreateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("register_password","toto");
        webRequest.addParameter("register2_password","toto");
        webRequest.addParameter("register_email","ludovic@xwiki.org");
        webRequest.addParameter("register_first_name","Ludovic");
        webRequest.addParameter("register_last_name","Dubost");

        MyFormAuthentication auth = new MyFormAuthentication("admin", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);
    }

    public void endCreateUserNoRight(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertTrue("User should not exist", doc.isNew());
        } finally {
            clientTearDown();
        }

    }

    public void testCreateUserNoRight() throws Throwable {
        launchTest();
    }


    public void beginCreateUserFail(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);

        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("Admin", map, "", "", "view, edit", context);
        updateRight("XWiki.XWikiPreferences", "XWiki.Admin", "", "register", true, true);

        String content = Utils.content1;
        Utils.content1 = "$xwiki.createUser()";
        Utils.createDoc(hibstore, "Main", "CreateUserTest", context);
        Utils.content1 = content;
        setUrl(webRequest, "view", "CreateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("register_password","toto");
        webRequest.addParameter("register2_password","tata");
        webRequest.addParameter("register_email","ludovic@xwiki.org");
        webRequest.addParameter("register_first_name","Ludovic");
        webRequest.addParameter("register_last_name","Dubost");

        MyFormAuthentication auth = new MyFormAuthentication("admin", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);
    }

    public void endCreateUserFail(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertTrue("User should not exist", doc.isNew());
        } finally {
            clientTearDown();
        }
    }

    public void testCreateUserFail() throws Throwable {
        launchTest();
    }

    public void beginCreateUser(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);
        String content = Utils.content1;
        Utils.content1 = "$xwiki.createUser()";
        Utils.createDoc(hibstore, "Main", "CreateUserTest", context);
        Utils.content1 = content;

        // In order for createUser to work, we need programming right
        Utils.createDoc(hibstore, "XWiki", "XWikiPreferences", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("Admin", map, "", "", "view, edit", context);
        updateRight("XWiki.XWikiPreferences", "XWiki.Admin", "", "register", true, true);

        setUrl(webRequest, "view", "CreateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("register_password","toto");
        webRequest.addParameter("register2_password","toto");
        webRequest.addParameter("register_email","ludovic@pobox.com");
        webRequest.addParameter("register_first_name","Ludovic");
        webRequest.addParameter("register_last_name","Dubost");

        MyFormAuthentication auth = new MyFormAuthentication("admin", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);
    }

    public void endCreateUser(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertFalse("User should exist", doc.isNew());
            assertEquals("Password is wrong", "toto", doc.getObject("XWiki.XWikiUsers",0).getStringValue("password"));
            assertEquals("Email is wrong", "ludovic@pobox.com", doc.getObject("XWiki.XWikiUsers",0).getStringValue("email"));
            assertEquals("First name is wrong", "Ludovic", doc.getObject("XWiki.XWikiUsers",0).getStringValue("first_name"));
            assertEquals("Last name is wrong", "Dubost", doc.getObject("XWiki.XWikiUsers",0).getStringValue("last_name"));
            assertEquals("Activity is wrong", 1, doc.getObject("XWiki.XWikiUsers",0).getIntValue("active"));

            XWikiRightService rightService = xwiki.getRightService();
            assertTrue("View Access should be allowed",
                        rightService.hasAccessLevel("view", "xwikitest:XWiki.LudovicDubost", "xwikitest:XWiki.LudovicDubost", context));
        } finally {
            clientTearDown();
        }
    }

    public void testCreateUser() throws Throwable {
        launchTest(false);
    }

    public void beginCreateUserProg(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            StoreHibernateTest.cleanUp(hibstore, context);
            clientSetUp(hibstore);
            String content = Utils.content1;
            Utils.content1 = "$xwiki.createUser()";
            Utils.createDoc(hibstore, "Main", "CreateUserTest", context);
            Utils.content1 = content;

            // In order for createUser to work, we need programming right
            Utils.createDoc(hibstore, "XWiki", "XWikiPreferences", context);
            HashMap map = new HashMap();
            map.put("password", "toto");
            xwiki.createUser("Admin", map, "", "", "view, edit", context);
            updateRight("XWiki.XWikiPreferences", "XWiki.LudovicDubost", "", "programming", true, true);

            setUrl(webRequest, "view", "CreateUserTest");
            webRequest.addParameter("xwikiname","LudovicDubost");
            webRequest.addParameter("register_password","toto");
            webRequest.addParameter("register2_password","toto");
            webRequest.addParameter("register_email","ludovic@pobox.com");
            webRequest.addParameter("register_first_name","Ludovic");
            webRequest.addParameter("register_last_name","Dubost");
        }

        public void endCreateUserProg(WebResponse webResponse) throws XWikiException, HibernateException {
            try {
                assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
                XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
                XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
                doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
                assertFalse("User should exist", doc.isNew());
                assertEquals("Password is wrong", "toto", doc.getObject("XWiki.XWikiUsers",0).getStringValue("password"));
                assertEquals("Email is wrong", "ludovic@pobox.com", doc.getObject("XWiki.XWikiUsers",0).getStringValue("email"));
                assertEquals("First name is wrong", "Ludovic", doc.getObject("XWiki.XWikiUsers",0).getStringValue("first_name"));
                assertEquals("Last name is wrong", "Dubost", doc.getObject("XWiki.XWikiUsers",0).getStringValue("last_name"));
                assertEquals("Activity is wrong", 1, doc.getObject("XWiki.XWikiUsers",0).getIntValue("active"));

                XWikiRightService rightService = xwiki.getRightService();
                assertTrue("View Access should be allowed",
                            rightService.hasAccessLevel("view", "xwikitest:XWiki.LudovicDubost", "xwikitest:XWiki.LudovicDubost", context));
            } finally {
                clientTearDown();
            }
        }

        public void testCreateUserProg() throws Throwable {
            launchTest();
        }

    public void beginCreateUserWithManyUsers(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);

        // Let's add the global user to the local AllGroup
        // Utils.addManyMembers(xwiki, context, "XWiki.LudovicDubost", "XWiki.XWikiAllGroup", 500);
        Utils.addMember(xwiki, context, "XWiki.LudovicDubost", "XWiki.XWikiAllGroup");

        String content = Utils.content1;
        Utils.content1 = "$xwiki.createUser()";
        Utils.createDoc(hibstore, "Main", "CreateUserTest", context);
        Utils.content1 = content;

        // In order for createUser to work, we need programming right
        Utils.createDoc(hibstore, "XWiki", "XWikiPreferences", context);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("Admin", map, "", "", "view, edit", context);
        updateRight("XWiki.XWikiPreferences", "XWiki.Admin", "", "register", true, true);

        setUrl(webRequest, "view", "CreateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("register_password","toto");
        webRequest.addParameter("register2_password","toto");
        webRequest.addParameter("register_email","ludovic@pobox.com");
        webRequest.addParameter("register_first_name","Ludovic");
        webRequest.addParameter("register_last_name","Dubost");

        MyFormAuthentication auth = new MyFormAuthentication("admin", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);
        context.put("timer", new Date());
    }

    public void endCreateUserWithManyUsers(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            Date starttime = (Date) context.get("timer");
            Date endtime = new Date();
            long delay = endtime.getTime() - starttime.getTime();

            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertFalse("User should exist", doc.isNew());
            assertEquals("Password is wrong", "toto", doc.getObject("XWiki.XWikiUsers",0).getStringValue("password"));
            assertEquals("Email is wrong", "ludovic@pobox.com", doc.getObject("XWiki.XWikiUsers",0).getStringValue("email"));
            assertEquals("First name is wrong", "Ludovic", doc.getObject("XWiki.XWikiUsers",0).getStringValue("first_name"));
            assertEquals("Last name is wrong", "Dubost", doc.getObject("XWiki.XWikiUsers",0).getStringValue("last_name"));
            assertEquals("Activity is wrong", 1, doc.getObject("XWiki.XWikiUsers",0).getIntValue("active"));

            XWikiRightService rightService = xwiki.getRightService();
            assertTrue("View Access should be allowed",
                        rightService.hasAccessLevel("view", "xwikitest:XWiki.LudovicDubost", "xwikitest:XWiki.LudovicDubost", context));
            assertTrue("Creation delay is way too long (over 60s)", (delay < 60000));
        } finally {
            clientTearDown();
        }
    }

    public void testCreateUserWithManyUsers() throws Throwable {
        launchTest(false);
    }


    private void prepareEmailPreferences(String pagename, String pagecontent, XWikiHibernateStore hibstore) throws XWikiException {
        String content = Utils.content1;
        Utils.content1 = pagecontent;
        Utils.createDoc(hibstore, "Main", pagename, context);
        Utils.content1 = content;
        // In order for createUser to work, we need programming right
        Utils.createDoc(hibstore, "XWiki", "XWikiPreferences", context);
        Utils.setStringValue("XWiki.XWikiPreferences", "XWiki.XWikiPreferences", "admin_email", "ludovic@xwiki.org", context);
        Utils.setStringValue("XWiki.XWikiPreferences", "XWiki.XWikiPreferences", "validation_email_content",
                        "Subject: Welcome to XWiki\n\nTest email from $sender to $email\n\nClick on http://www.xwiki.com/xwiki/bin/view/XWiki/InscriptionStep2?validkey=$validkey", context);
        Utils.setStringValue("XWiki.XWikiPreferences", "XWiki.XWikiPreferences", "confirmation_email_content",
                "Subject: Welcome to XWiki\n\nConfirmation email from $sender to $email\n\nYour password is $password", context);
        Utils.setStringValue("XWiki.XWikiPreferences", "XWiki.XWikiPreferences", "smtp_server", "127.0.0.1", context);
        Utils.setStringValue("XWiki.XWikiPreferences", "XWiki.XWikiPreferences", "smtp_port", "225", context);
    }


    public void beginCreateUserWithEmail(WebRequest webRequest) throws HibernateException, XWikiException, MalformedURLException {

        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);

        prepareEmailPreferences("CreateUserTest", "$xwiki.createUser(true)", hibstore);
        HashMap map = new HashMap();
        map.put("password", "toto");
        xwiki.createUser("Admin", map, "", "", "view, edit", context);
        updateRight("XWiki.XWikiPreferences", "XWiki.Admin", "", "register", true, true);

        setUrl(webRequest, "view", "CreateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("register_password","toto");
        webRequest.addParameter("register2_password","toto");
        webRequest.addParameter("register_email","ludovic@xwiki.org");
        webRequest.addParameter("register_first_name","Ludovic");
        webRequest.addParameter("register_last_name","Dubost");

        MyFormAuthentication auth = new MyFormAuthentication("admin", "toto");
        auth.setSecurityCheckURL(new URL("http://127.0.0.1:9080/xwiki/testbin/login/XWiki/XWikiLogin"));
        webRequest.setAuthentication(auth);

        // Let's start the email server
        startSmtpServer();
        assertNotNull("Could not start email server for tests", server);
    }

    public void endCreateUserWithEmail(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            SmtpMessage email = getLastMessage();
            stopSmtpServer();

            // Let's check the email
            assertNotNull("Email could not be retrieved", email);
            assertEquals("Email subject is not correct", "Welcome to XWiki", email.getHeaderValue("Subject"));
            assertTrue("Email body is not correct", email.getBody().startsWith("Test email from ludovic@xwiki.org to ludovic@xwiki.org\n\nClick on http://www.xwiki.com/xwiki/bin/view/XWiki/InscriptionStep2?validkey="));

            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertFalse("User should exist", doc.isNew());
            assertEquals("Password is wrong", "toto", doc.getObject("XWiki.XWikiUsers",0).getStringValue("password"));
            assertEquals("Email is wrong", "ludovic@xwiki.org", doc.getObject("XWiki.XWikiUsers",0).getStringValue("email"));
            assertEquals("First name is wrong", "Ludovic", doc.getObject("XWiki.XWikiUsers",0).getStringValue("first_name"));
            assertEquals("Last name is wrong", "Dubost", doc.getObject("XWiki.XWikiUsers",0).getStringValue("last_name"));
            assertEquals("Activity is wrong", 0, doc.getObject("XWiki.XWikiUsers",0).getIntValue("active"));

            String validkey = doc.getObject("XWiki.XWikiUsers",0).getStringValue("validkey");
            assertEquals("Validation Key length is not correct", 16, validkey.length());
            assertTrue("Validation Key is not correct in email", (email.getBody().indexOf(validkey)!=-1));
        } finally {
            stopSmtpServer();
            clientTearDown();
        }
    }

    public void testCreateUserWithEmail() throws Throwable {
        launchTest(false);
    }


    public void beginValidateUserWithEmailFail(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);

        prepareEmailPreferences("ValidateUserTest", "$xwiki.validateUser(true)", hibstore);
        Utils.createDoc(hibstore, "XWiki", "LudovicDubost", context);
        Utils.setStringValue("XWiki.LudovicDubost", "XWiki.XWikiUsers", "email", "ludovic@xwiki.org", context);
        Utils.setStringValue("XWiki.LudovicDubost", "XWiki.XWikiUsers", "validkey", "ABCDEFGHIJKLMNOP", context);
        Utils.setStringValue("XWiki.LudovicDubost", "XWiki.XWikiUsers", "password", "toto", context);

        setUrl(webRequest, "view", "ValidateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("validkey","ABCDEFGHIJKLMNOPQ");

        // Let's start the email server
        startSmtpServer();
        assertNotNull("Could not start email server for tests", server);
    }

    public void endValidateUserWithEmailFail(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            SmtpMessage email = getLastMessage();
            stopSmtpServer();

            // Let's check the email
            assertNull("Email could not be retrieved", email);
            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertFalse("User should exist", doc.isNew());
            assertEquals("Password is wrong", "toto", doc.getObject("XWiki.XWikiUsers",0).getStringValue("password"));
            assertEquals("Email is wrong", "ludovic@xwiki.org", doc.getObject("XWiki.XWikiUsers",0).getStringValue("email"));
            assertEquals("Activity is wrong", 0, doc.getObject("XWiki.XWikiUsers",0).getIntValue("active"));
        } finally {
            stopSmtpServer();
            clientTearDown();
        }
    }

    public void testValidateUserWithEmailFail() throws Throwable {
         launchTest();
    }



    public void beginValidateUserWithEmail(WebRequest webRequest) throws HibernateException, XWikiException {
        XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
        StoreHibernateTest.cleanUp(hibstore, context);
        clientSetUp(hibstore);

        prepareEmailPreferences("ValidateUserTest", "$xwiki.validateUser(true)",hibstore);
        Utils.createDoc(hibstore, "XWiki", "LudovicDubost", context);
        Utils.setStringValue("XWiki.LudovicDubost", "XWiki.XWikiUsers", "email", "ludovic@xwiki.org", context);
        Utils.setStringValue("XWiki.LudovicDubost", "XWiki.XWikiUsers", "password", "toto", context);
        Utils.setStringValue("XWiki.LudovicDubost", "XWiki.XWikiUsers", "validkey", "ABCDEFGHIJKLMNOP", context);

        setUrl(webRequest, "view", "ValidateUserTest");
        webRequest.addParameter("xwikiname","LudovicDubost");
        webRequest.addParameter("validkey","ABCDEFGHIJKLMNOP");

        // Let's start the email server
        startSmtpServer();
        assertNotNull("Could not start email server for tests", server);
    }

    public void endValidateUserWithEmail(WebResponse webResponse) throws XWikiException, HibernateException {
        try {
            SmtpMessage email = getLastMessage();
            stopSmtpServer();

            // Let's check the email
            assertNotNull("Email could not be retrieved", email);
            assertEquals("Email subject is not correct", "Welcome to XWiki", email.getHeaderValue("Subject"));
            assertTrue("Email body is not correct", email.getBody().startsWith("Confirmation email from ludovic@xwiki.org to ludovic@xwiki.org\n\nYour password is toto"));

            assertEquals("Response status should be 200", 200, webResponse.getStatusCode());
            XWikiHibernateStore hibstore = new XWikiHibernateStore(getHibpath());
            XWikiDocument doc = new XWikiDocument("XWiki", "LudovicDubost");
            doc = (XWikiDocument) hibstore.loadXWikiDoc(doc, context);
            assertFalse("User should exist", doc.isNew());
            assertEquals("Password is wrong", "toto", doc.getObject("XWiki.XWikiUsers",0).getStringValue("password"));
            assertEquals("Email is wrong", "ludovic@xwiki.org", doc.getObject("XWiki.XWikiUsers",0).getStringValue("email"));
            assertEquals("Activity is wrong", 1, doc.getObject("XWiki.XWikiUsers",0).getIntValue("active"));
        } finally {
            stopSmtpServer();
            clientTearDown();
        }
    }

    public void testValidateUserWithEmail() throws Throwable {
        launchTest(false);
    }

}