<!--
   $HeadURL: https://code.library.ualberta.ca/svn/repos/era/trunk/web/jsp/protected/maintenanceForms.jsp $
   $Id: maintenanceForms.jsp 5427 2012-07-12 20:30:12Z pcharoen $
   $Revision: 5427 $ $Date: 2012-07-12 14:30:12 -0600 (Thu, 12 Jul 2012) $
   Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
-->
<%@ include file="/jsp/layout/taglibs.jspf"%>
<stripes:layout-render name="/jsp/layout/system.jsp">
	<stripes:layout-component name="contents">
		<div>
			<c:choose>
				<c:when test="${event == 'getobjectxml'}">
					<h3>Get Object XML</h3>
					<form action="/dev/main" method="post" target="_blank">
						<ul>
							<li>
								<div class="left_col"><label>PID</label>:</div>
								<div class="right_col"><input type="text" name="params" size="42" /></div>
							</li>
						</ul>
						<input type="submit" name="getobjectxml" value="Submit" />
					</form>
				</c:when>
				<c:when test="${event == 'oaigetrecord'}">
					<h3>Get OAI Record</h3>
					<form action="/dev/main" method="post" target="_blank">
						<ul>
							<li>
								<div class="left_col">
									<b>metadataPrefix</b>:
								</div>
								<div class="right_col">
									<select name="params">
										<option value="oai_dc">oai_dc</option>
										<option value="oai_etdms">oai_etdms</option>
									</select>
								</div>
							</li>
							<li>
								<div class="left_col">
									<b>identifier</b>:
								</div>
								<div class="right_col">
									<input type="text" name="params" size="42" />
								</div>
							</li>
						</ul>					
						<input type="submit" name="oaigetrecord" value="Submit" />
					</form>
				</c:when>
				<c:when test="${event == 'subscriptionbydate'}">
					<h3>Subscription by Date</h3>
					<form action="/dev/main" method="post">
						<ul>
							<li>
								<div class="left_col"><label>Accepted Date (yyyy/MM/dd)</label>:</div>
								<div class="right_col"><input type="text" name="params" size="42" /></div>
							</li>
							<li>
								<div class="left_col"><label>Username</label>:</div>
								<div class="right_col"><input type="text" name="params" size="42" /></div>
							</li>
							<li>
								<div class="left_col">&nbsp;</div>
								<div class="right_col">
									<input type="submit" name="subscriptionbydate" value="Submit" />
								</div>
							</li>
						</ul>
					</form>
				</c:when>
				<c:otherwise>
					<div style="color: red;">Action form not found ($event)!</div>
				</c:otherwise>
			</c:choose>
		</div>
		<div style="margin-top: 1em; clear: both;">
			<a href="/dev/main/init"><b>back</b></a>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
