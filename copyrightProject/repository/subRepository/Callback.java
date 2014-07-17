//===========================Callback.java============================//
//  Author:    Connor Group Software Development team 2014            //
//  Copyright: Copyright (C) 2014 Connor Group. All rights reserved.  //
//  Email:     it@connorgp.com                                        //
//  Website:   http://www.connorgp.com                                //
//                                                                    //
//  The copyright to the source code and computer program(s)          //
//  herein is the property of Connor Group.The source code            //
//  and program(s) may be used and/or copied only with the            //
//  written permission of Connor Group or in accordance with          //
//  the terms and conditions stipulated in the                        //
//  agreement/contract under which the source code and                //
//  program(s) have been supplied.                                    //
//                                                                    //
package com.mycila.maven.plugin.license;
import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.header.Header;

/**
 * <b>Date:</b> 18-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public interface Callback {
    void onHeaderNotFound(Document document, Header header);

    void onExistingHeader(Document document, Header header);
}
