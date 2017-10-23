FROM tomee:8-jre-7.0.3-plus

RUN rm -Rf /usr/local/tomee/webapps/*

COPY app-lib/custom-resource-${project.version}.jar /usr/local/tomee/lib/
COPY app-lib/javaee-api-7.0.jar /usr/local/tomee/lib/
COPY app-lib/properties-provider-${project.version}.jar /usr/local/tomee/lib/

COPY simple-app.war /usr/local/tomee/webapps/ROOT.war
