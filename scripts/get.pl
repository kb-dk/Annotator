#!/usr/bin/perl

use URI::Escape;
use LWP::Simple;
 
my $uri_string = "http://www.kb.dk/da/index.html";
my $encoded    = uri_escape($uri_string);
# my $source   = "http://localhost:8080/annotation/".$encoded.'/tag';
# my $source   = "http://localhost:8080/annotation/".$encoded.'/comment';
# my $source     = "http://localhost:8080/annotation/".$encoded.'/xlink';
my $source     = "http://localhost:8080/annotation/".$encoded.'';
print STDERR "$source\n";
$content       = get($source);
die "Couldn’t get it!" unless defined $content;

print $content;
