/*
 * Copyright (C) 2013 soqqo ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.straight

import java.net.URL
import org.eclipse.jetty.server.{ Server => JettyServer }
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.FileResource
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.HandlerList
import spray.servlet.{ Servlet30ConnectorServlet, Initializer }

object JettyContainer extends App {

    val jettyServer = new JettyServer(8080);
  
  // servlets
  val context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

  context.setContextPath("/")
  context.setResourceBase("src/main/webapp")
 
  val sprayInit = new Initializer()
  context.addEventListener(sprayInit)

  val spray = new ServletHolder(new Servlet30ConnectorServlet)
  spray.setInitParameter("async-supported", "true")
  context.addServlet(spray, "/api/*")

  
  // static resource Servlet
  val resourceHandler = new ResourceHandler()
  resourceHandler.setDirectoriesListed(true);
  resourceHandler.setWelcomeFiles(Array( "index.html" ));
  resourceHandler.setResourceBase("src/main/webapp")
  
  val handlers = new HandlerList()
  handlers.setHandlers(Array(context,resourceHandler))
  jettyServer.setHandler(handlers)
  jettyServer.start()
  jettyServer.join()

}
