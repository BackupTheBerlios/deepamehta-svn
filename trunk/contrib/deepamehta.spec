Name: deepamehta
Summary: DeepaMehta -- The Semantic Desktop
Version: 2.0b6
Release: 1
License: Copyright DeepaMehta Project
Group: System Environment/Libraries
URL: http://www.deepamehta.de
Distribution: SuSE 10.0 (i586)
Source0: http://www.deepamehta.de/deepamehta-2.0b6.zip
Source1: http://www.deepamehta.de/dm-libs.zip
Source2: http://www.deepamehta.de/deepamehta-2.0b6-apidocs.zip
# DROP DATABASES; appropriate local paths for WEB Server utilities
Patch0: deepamehta.patch
Patch1: deepamehta-root.patch
BuildRequires: unzip ant java mysql
BuildRoot: %{_tmppath}/%{name}-%{version}-root
BuildArch: i586

%description
This package contains the DeepaMehta Semantic Desktop, based on Topic Maps.

%prep
mkdir -p dm
cd dm
unzip -o %{S:0}
unzip -o %{S:1}
unzip -o %{S:2}
# Aargh, completely incomprehensible access rights!
chmod -R u+w *
%patch0
%patch1
# Missing directory for build
mkdir -p install/examples/messageboard/build

%build
echo ">>> Don't forget to start the MySQL server!!! <<<"
cd dm
CLASSPATH=$RPM_BUILD_DIR/dm/install/server/DeepaMehtaService.jar:\
$RPM_BUILD_DIR/dm/install/server/DeepaMehtaTopics.jar:\
$RPM_BUILD_DIR/dm/dm-libs/servlet.jar:$CLASSPATH; ant install

%install
rm -rf $RPM_BUILD_ROOT

%clean
rm -rf $RPM_BUILD_ROOT

%changelog
* Sun Jan 29 2006 Andreas Scherer <andreas_freenet@freenet.de>
- Initial build
