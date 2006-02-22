Name: deepamehta
Summary: DeepaMehta -- The Semantic Desktop
Version: 2.0b7
Release: 1
License: Copyright DeepaMehta Project
Group: User Interface/Desktops
Packager: Andreas Scherer <andreas_hacker@freenet.de>
URL: http://www.deepamehta.de
Distribution: SuSE 10.0 (i586)
Source0: http://www.deepamehta.de/deepamehta-2.0b7.tar.bz2
Source1: instances.xml
Patch0: deepamehta.patch
Patch1: deepamehta-root.patch
BuildRequires: java
BuildRequires: ant
BuildRequires: jaf
BuildRequires: avalon-framework
BuildRequires: fop
BuildRequires: jakarta-commons-beanutils
BuildRequires: jakarta-commons-collections
BuildRequires: jakarta-commons-digester
BuildRequires: jakarta-commons-fileupload
BuildRequires: jakarta-commons-logging
BuildRequires: log4j
BuildRequires: javamail
BuildRequires: mysql-connector-java
BuildRequires: xalan-j2
BuildRequires: xerces-j2
BuildRoot: %{_tmppath}/%{name}-%{version}-root
BuildArch: i586

%define installpath /usr/share/%{name}-%{version}

%description
This package contains the DeepaMehta Semantic Desktop, based on Topic Maps.

%prep
%setup -q -n %{name}
%patch0
%patch1
cp %{S:1} $RPM_BUILD_DIR/%{name}

%build
ant build

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/%{installpath}/libs
mkdir -p $RPM_BUILD_ROOT/%{installpath}/{backgrounds,icons,images,sounds,stylesheets}
mkdir -p $RPM_BUILD_ROOT/%{installpath}/install/db
cp bin/*.jar $RPM_BUILD_ROOT/%{installpath}
cp build.xml config.xml instances.xml plugins.xml $RPM_BUILD_ROOT/%{installpath}
cp libs/commons-cli-1.0.jar $RPM_BUILD_ROOT/%{installpath}/libs
cp libs/googleapi.jar $RPM_BUILD_ROOT/%{installpath}/libs
cp libs/jimi-1.0.jar $RPM_BUILD_ROOT/%{installpath}/libs
cp libs/jwf-1.0.1.jar $RPM_BUILD_ROOT/%{installpath}/libs
cp libs/xml4j.jar $RPM_BUILD_ROOT/%{installpath}/libs
cp develop/data/backgrounds/*.png $RPM_BUILD_ROOT/%{installpath}/backgrounds
cp develop/data/icons/*.gif $RPM_BUILD_ROOT/%{installpath}/icons
cp develop/data/images/*.gif $RPM_BUILD_ROOT/%{installpath}/images
cp develop/data/sounds/*.au $RPM_BUILD_ROOT/%{installpath}/sounds
cp develop/data/stylesheets/*.xsl $RPM_BUILD_ROOT/%{installpath}/stylesheets
cp develop/data/DefaultContents.xml $RPM_BUILD_ROOT/%{installpath}
cp install/db/*.sql $RPM_BUILD_ROOT/%{installpath}/install/db

%post
ant -f %{installpath}/build.xml createdb
ant -f %{installpath}/build.xml createtables
ant -f %{installpath}/build.xml initdb

%preun
ant -f %{installpath}/build.xml dropdb

%files
%defattr(-,root,root,-)
/usr/share/%{name}-%{version}

%clean
rm -rf $RPM_BUILD_ROOT

%changelog
* Wed Feb 22 2006 Andreas Scherer <andreas_hacker@freenet.de>
- Use system JAR libraries as far as possible.
* Sun Feb 19 2006 Andreas Scherer <andreas_hacker@freenet.de>
- Initial build
