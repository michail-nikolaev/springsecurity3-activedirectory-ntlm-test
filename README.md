http://xm.renz.com.cn
Spring Security 3 + Active Directory + NTLMv2
===========================================

Test for usage Spring Security 3 together with AD and NTLM authentification

Check next resources to get it worked (but use SetComputerAccountPasswordWindows7.vbs):


http://issues.liferay.com/browse/LPS-11391

http://www.liferay.com/community/wiki?p_p_id=36&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-2&p_p_col_count=1&_36_struts_action=%2Fwiki%2Fview_page_attachments&p_r_p_185834411_nodeId=1071674&p_r_p_185834411_title=NTLMv2+SSO+Configuration

http://ntlmv2auth.sourceforge.net/ntlmv2-demoapp/index.html

http://issues.liferay.com/browse/LPS-15380?focusedCommentId=135462&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-135462

http://www.liferay.com/community/forums/-/message_boards/message/10737885

To run use "gradle jettyRun" or "tomcatRun"

Check activedirectory.properties for settings.

