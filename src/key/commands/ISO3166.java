/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
**
**  $Id: ISO3166.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class history
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  01Mar99     Koschei      start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class ISO3166 extends Command
{
  private static String[][] countries = {
    { "Afghanistan, Islamic State of"               , "af"},
    { "Albania"                                     , "al"},
    { "Algeria"                                     , "dz"},
    { "American Samoa"                              , "as"},
    { "Andorra, Pricipality of"                     , "ad"},
    { "Angola, Republic of"                         , "ao"},
    { "Anguilla"                                    , "ai"},
    { "Antarctica"                                  , "aq"},
    { "Antigua and Barbuda"                         , "ag"},
    { "Argentina"                                   , "ar"},
    { "Armenia"                                     , "am"},
    { "Aruba"                                       , "aw"},
    { "Australia"                                   , "au"},
    { "Austria"                                     , "at"},
    { "Azerbaijan"                                  , "az"},
    { "Bahamas"                                     , "bs"},
    { "Bahrain"                                     , "bh"},
    { "Bangladesh"                                  , "bd"},
    { "Barbados"                                    , "bb"},
    { "Belarus"                                     , "by"},
    { "Belgium"                                     , "be"},
    { "Belize"                                      , "bz"},
    { "Benin"                                       , "bj"},
    { "Bermuda"                                     , "bm"},
    { "Bhutan"                                      , "bt"},
    { "Bolivia"                                     , "bo"},
    { "Bosnia and Herzegowina"                      , "ba"},
    { "Botswana"                                    , "bw"},
    { "Bouvet Island"                               , "bv"},
    { "Brazil"                                      , "br"},
    { "British Indian Ocean Territory"              , "io"},
    { "Brunei Darussalam"                           , "bn"},
    { "Bulgaria"                                    , "bg"},
    { "Burkina faso"                                , "bf"},
    { "Burundi"                                     , "bi"},
    { "Cambodia, Kindom of"                         , "kh"},
    { "Cameroon"                                    , "cm"},
    { "Canada"                                      , "ca"},
    { "Cape verde"                                  , "cv"},
    { "Cayman islands"                              , "ky"},
    { "Central African Republic"                    , "cf"},
    { "Chad"                                        , "td"},
    { "Chile"                                       , "cl"},
    { "China"                                       , "cn"},
    { "Christmas Island"                            , "cx"},
    { "Cocos (keeling) Islands"                     , "cc"},
    { "Colombia"                                    , "co"},
    { "Comoros"                                     , "km"},
    { "Congo"                                       , "cg"},
    { "Congo, the Democratic Republic of the"       , "cd"},
    { "Cook Islands"                                , "ck"},
    { "Costa Rica"                                  , "cr"},
    { "Cote d'ivoire (Ivory Coast)"                 , "ci"},
    { "Croatia (Hrvatska)"                          , "hr"},
    { "Cuba"                                        , "cu"},
    { "Cyprus"                                      , "cy"},
    { "Czech Republic"                              , "cz"},
    { "Czechoslovakia (Officially deleted)"         , "cs"},
    { "Denmark"                                     , "dk"},
    { "Djibouti"                                    , "dj"},
    { "Dominica"                                    , "dm"},
    { "Dominican Republic"                          , "do"},
    { "East Timor"                                  , "tp"},
    { "Ecuador"                                     , "ec"},
    { "Egypt"                                       , "eg"},
    { "El Salvador"                                 , "sv"},
    { "Equatorial Guinea"                           , "gq"},
    { "Eritrea"                                     , "er"},
    { "Estonia"                                     , "ee"},
    { "Ethiopia"                                    , "et"},
    { "Falkland Islands (Malvinas)"                 , "fk"},
    { "Faroe Islands"                               , "fo"},
    { "Fiji"                                        , "fj"},
    { "Finland"                                     , "fi"},
    { "France"                                      , "fr"},
    { "France, metropolitan"                        , "fx"},
    { "French Guiana"                               , "gf"},
    { "French Polynesia"                            , "pf"},
    { "French Southern Territories"                 , "tf"},
    { "Gabon"                                       , "ga"},
    { "Gambia"                                      , "gm"},
    { "Georgia"                                     , "ge"},
    { "Germany"                                     , "de"},
    { "Ghana"                                       , "gh"},
    { "Gibraltar"                                   , "gi"},
    { "Greece"                                      , "gr"},
    { "Greenland"                                   , "gl"},
    { "Great Britain"                               , "gb"},
    { "Great Britain (iso 3166 code is gb)"         , "uk"},
    { "Grenada"                                     , "gd"},
    { "Guadeloupe"                                  , "gp"},
    { "Guam"                                        , "gu"},
    { "Guatemala"                                   , "gt"},
    { "Guinea"                                      , "gn"},
    { "Guinea-Bissau"                               , "gw"},
    { "Guyana"                                      , "gy"},
    { "Haiti"                                       , "ht"},
    { "Heard and MC Donald Islands"                 , "hm"},
    { "Holy See (Vatican City State)"               , "va"},
    { "Honduras"                                    , "hn"},
    { "Hong kong"                                   , "hk"},
    { "Hungary"                                     , "hu"},
    { "Iceland"                                     , "is"},
    { "India"                                       , "in"},
    { "Indonesia"                                   , "id"},
    { "Iran, Islamic Republic of"                   , "ir"},
    { "Iraq"                                        , "iq"},
    { "Ireland"                                     , "ie"},
    { "Israel"                                      , "il"},
    { "Italy"                                       , "it"},
    { "Jamaica"                                     , "jm"},
    { "Japan"                                       , "jp"},
    { "Jordan"                                      , "jo"},
    { "Kazakhstan"                                  , "kz"},
    { "Kenya"                                       , "ke"},
    { "Kiribati"                                    , "ki"},
    { "Korea, Democratic People's Republic of"      , "kp"},
    { "Korea, Republic of"                          , "kr"},
    { "Kuwait"                                      , "kw"},
    { "Kyrgyzstan (Kyrgyz Republic)"                , "kg"},
    { "Lao People's Democratic Republic"            , "la"},
    { "Latvia"                                      , "lv"},
    { "Lebanon"                                     , "lb"},
    { "Lesotho"                                     , "ls"},
    { "Liberia"                                     , "lr"},
    { "Libyan Arab Jamahiriya"                      , "ly"},
    { "Liechtenstein"                               , "li"},
    { "Lithuania"                                   , "lt"},
    { "Luxembourg"                                  , "lu"},
    { "Macau"                                       , "mo"},
    { "Macedonia, the former Yugoslav Republic of"  , "mk"},
    { "Madagascar, Replublic of"                    , "mg"},
    { "Malawi"                                      , "mw"},
    { "Malaysia"                                    , "my"},
    { "Maldives"                                    , "mv"},
    { "Mali"                                        , "ml"},
    { "Malta"                                       , "mt"},
    { "Marshall Islands"                            , "mh"},
    { "Martinique"                                  , "mq"},
    { "Mauritania"                                  , "mr"},
    { "Mauritius"                                   , "mu"},
    { "Mayotte"                                     , "yt"},
    { "Mexico"                                      , "mx"},
    { "Micronesia, Federated States of"             , "fm"},
    { "Moldova, Republic of"                        , "md"},
    { "Monaco"                                      , "mc"},
    { "Mongolia"                                    , "mn"},
    { "Montserrat"                                  , "ms"},
    { "Morocco"                                     , "ma"},
    { "Mozambique"                                  , "mz"},
    { "Myanmar"                                     , "mm"},
    { "Namibia"                                     , "na"},
    { "Nauru"                                       , "nr"},
    { "Nepal"                                       , "np"},
    { "Netherlands"                                 , "nl"},
    { "Netherlands Antilles"                        , "an"},
    { "Neutral zone (Officially deleted)"           , "nt"},
    { "New Caledonia"                               , "nc"},
    { "New Zealand"                                 , "nz"},
    { "Nicaragua"                                   , "ni"},
    { "Niger"                                       , "ne"},
    { "Nigeria"                                     , "ng"},
    { "Niue"                                        , "nu"},
    { "Norfolk Island"                              , "nf"},
    { "Northern Mariana Islands"                    , "mp"},
    { "Norway"                                      , "no"},
    { "Oman"                                        , "om"},
    { "Pakistan"                                    , "pk"},
    { "Palau"                                       , "pw"},
    { "Panama"                                      , "pa"},
    { "Papua New Guinea"                            , "pg"},
    { "Paraguay"                                    , "py"},
    { "Peru"                                        , "pe"},
    { "Philippines"                                 , "ph"},
    { "Pitcairn"                                    , "pn"},
    { "Poland"                                      , "pl"},
    { "Portugal"                                    , "pt"},
    { "Puerto Rico"                                 , "pr"},
    { "Qatar"                                       , "qa"},
    { "Reunion"                                     , "re"},
    { "Romania"                                     , "ro"},
    { "Russian Federation"                          , "ru"},
    { "Rwanda"                                      , "rw"},
    { "Saint Kitts and Nevis"                       , "kn"},
    { "Saint Lucia"                                 , "lc"},
    { "Saint Vincent and the Grenadines"            , "vc"},
    { "Samoa"                                       , "ws"},
    { "San Marino"                                  , "sm"},
    { "Sao Tome and Principe"                       , "st"},
    { "Saudi Arabia"                                , "sa"},
    { "Senegal"                                     , "sn"},
    { "Seychelles"                                  , "sc"},
    { "Sierra Leone"                                , "sl"},
    { "Singapore"                                   , "sg"},
    { "Slovakia (Slovak Republic)"                  , "sk"},
    { "Slovenia"                                    , "si"},
    { "Solomon Islands"                             , "sb"},
    { "Somalia"                                     , "so"},
    { "South Africa"                                , "za"},
    { "South Georgia and the South Sandwich Sslands", "gs"},
    { "Soviet Union (See Russian Federation)"       , "su"},
    { "Spain"                                       , "es"},
    { "Sri Lanka"                                   , "lk"},
    { "St. Helena"                                  , "sh"},
    { "St. Pierre and Miquelon"                     , "pm"},
    { "Sudan"                                       , "sd"},
    { "Suriname"                                    , "sr"},
    { "Svalbard and Jan Mayen Islands"              , "sj"},
    { "Swaziland"                                   , "sz"},
    { "Sweden"                                      , "se"},
    { "Switzerland"                                 , "ch"},
    { "Syrian Arab Republic"                        , "sy"},
    { "Taiwan, Province of China"                   , "tw"},
    { "Tajikistan"                                  , "tj"},
    { "Tanzania, United Republic of"                , "tz"},
    { "Thailand"                                    , "th"},
    { "Togo"                                        , "tg"},
    { "Tokelau"                                     , "tk"},
    { "Tonga"                                       , "to"},
    { "Trinidad and Tobago"                         , "tt"},
    { "Tunisia"                                     , "tn"},
    { "Turkey"                                      , "tr"},
    { "Turkmenistan"                                , "tm"},
    { "Turks and Caicos Islands"                    , "tc"},
    { "Tuvalu"                                      , "tv"},
    { "Uganda"                                      , "ug"},
    { "Ukraine"                                     , "ua"},
    { "United Arab Emirates"                        , "ae"},
    { "United Kingdom"                              , "gb"},
    { "United Kingdom (iso 3166 code is gb)"        , "uk"},
    { "United States"                               , "us"},
    { "United States minor outlying islands"        , "um"},
    { "Uruguay"                                     , "uy"},
    { "Uzbekistan"                                  , "uz"},
    { "Vanuatu"                                     , "vu"},
    { "Venezuela"                                   , "ve"},
    { "Viet Nam"                                    , "vn"},
    { "Virgin Islands (british)"                    , "vg"},
    { "Virgin Islands (u.s.)"                       , "vi"},
    { "Wallis and Futuna Islands"                   , "wf"},
    { "Western Sahara"                              , "eh"},
    { "Yemen"                                       , "ye"},
    { "Yugoslavia"                                  , "yu"},
    { "Zaire"                                       , "zr"},
    { "Zambia"                                      , "zm"},
    { "Zimbabwe"                                    , "zw"},
  };

  public ISO3166()
  {
    setKey( "iso3166 " );
    usage = "[\"all\"]|[code]|[country]";
  }

  public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
  {
    if( args.hasMoreTokens() )
    {
      String query = args.nextToken().toLowerCase();
      if( query.equalsIgnoreCase( "all-" ) )
      {
        for(int i=0;i<countries.length;i++) {
          ic.sendFeedback( countries[i][1] + " " + countries[i][0] );
        }
      }
      else if ( query.equalsIgnoreCase ("all") )
      {
	ic.sendError(" There are " + countries.length + " entries. If you really want to see them *all*, use \"all-\" instead of \"all\".");
      }
      else
      {
        boolean found = false;
	int search = 0;
	if (query.length() == 2)
	 search = 1;
        for(int i=0;i<countries.length;i++) {
          if (countries[i][search].toLowerCase().indexOf(query) != -1 ) {
              ic.sendFeedback( countries[i][1] + " " + countries[i][0] );
              found = true;
            }
        }
        if (!found) {
          ic.sendError(" No country of that description found.");
        }
      }
    }
    else
    {
      ic.sendFeedback( usage );
    }
  }
}
