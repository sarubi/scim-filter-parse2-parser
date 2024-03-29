/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jun 21 11:06:52 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.Stack;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

public class Parser
{
  private Parser() {}

  static public void main(String[] args)
  {
    Properties arguments = new Properties();
    String error = "";
    boolean ok = args.length > 0;

    if (ok)
    {
      arguments.setProperty("Trace", "Off");
      arguments.setProperty("Rule", "PATH");

      for (int i = 0; i < args.length; i++)
      {
        if (args[i].equals("-trace"))
          arguments.setProperty("Trace", "On");
        else if (args[i].equals("-visitor"))
          arguments.setProperty("Visitor", args[++i]);
        else if (args[i].equals("-file"))
          arguments.setProperty("File", args[++i]);
        else if (args[i].equals("-string"))
          arguments.setProperty("String", args[++i]);
        else if (args[i].equals("-rule"))
          arguments.setProperty("Rule", args[++i]);
        else
        {
          error = "unknown argument: " + args[i];
          ok = false;
        }
      }
    }

    if (ok)
    {
      if (arguments.getProperty("File") == null &&
          arguments.getProperty("String") == null)
      {
        error = "insufficient arguments: -file or -string required";
        ok = false;
      }
    }

    if (!ok)
    {
      System.out.println("error: " + error);
      System.out.println("usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]");
    }
    else
    {
      try
      {
        Rule rule = null;

        if (arguments.getProperty("File") != null)
        {
          rule = 
            parse(
              arguments.getProperty("Rule"), 
              new File(arguments.getProperty("File")), 
              arguments.getProperty("Trace").equals("On"));
        }
        else if (arguments.getProperty("String") != null)
        {
          rule = 
            parse(
              arguments.getProperty("Rule"), 
              arguments.getProperty("String"), 
              arguments.getProperty("Trace").equals("On"));
        }

        if (arguments.getProperty("Visitor") != null)
        {
          Visitor visitor = 
            (Visitor)Class.forName(arguments.getProperty("Visitor")).newInstance();
          rule.accept(visitor);
        }
      }
      catch (IllegalArgumentException e)
      {
        System.out.println("argument error: " + e.getMessage());
      }
      catch (IOException e)
      {
        System.out.println("io error: " + e.getMessage());
      }
      catch (ParserException e)
      {
        System.out.println("parser error: " + e.getMessage());
      }
      catch (ClassNotFoundException e)
      {
        System.out.println("visitor error: class not found - " + e.getMessage());
      }
      catch (IllegalAccessException e)
      {
        System.out.println("visitor error: illegal access - " + e.getMessage());
      }
      catch (InstantiationException e)
      {
        System.out.println("visitor error: instantiation failure - " + e.getMessage());
      }
    }
  }

  static public Rule parse(String rulename, String string)
  throws IllegalArgumentException,
         ParserException
  {
    return parse(rulename, string, false);
  }

  static public Rule parse(String rulename, InputStream in)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    return parse(rulename, in, false);
  }

  static public Rule parse(String rulename, File file)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    return parse(rulename, file, false);
  }

  static private Rule parse(String rulename, String string, boolean trace)
  throws IllegalArgumentException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (string == null)
      throw new IllegalArgumentException("null string");

    ParserContext context = new ParserContext(string, trace);

    Rule rule = null;
    if (rulename.equalsIgnoreCase("PATH")) rule = Rule_PATH.parse(context);
    else if (rulename.equalsIgnoreCase("attrPath")) rule = Rule_attrPath.parse(context);
    else if (rulename.equalsIgnoreCase("valuePath")) rule = Rule_valuePath.parse(context);
    else if (rulename.equalsIgnoreCase("valFilter")) rule = Rule_valFilter.parse(context);
    else if (rulename.equalsIgnoreCase("attrExp")) rule = Rule_attrExp.parse(context);
    else if (rulename.equalsIgnoreCase("filter")) rule = Rule_filter.parse(context);
    else if (rulename.equalsIgnoreCase("filterDash")) rule = Rule_filterDash.parse(context);
    else if (rulename.equalsIgnoreCase("compValue")) rule = Rule_compValue.parse(context);
    else if (rulename.equalsIgnoreCase("compareOp")) rule = Rule_compareOp.parse(context);
    else if (rulename.equalsIgnoreCase("ATTRNAME")) rule = Rule_ATTRNAME.parse(context);
    else if (rulename.equalsIgnoreCase("nameChar")) rule = Rule_nameChar.parse(context);
    else if (rulename.equalsIgnoreCase("subAttr")) rule = Rule_subAttr.parse(context);
    else if (rulename.equalsIgnoreCase("SP")) rule = Rule_SP.parse(context);
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = Rule_ALPHA.parse(context);
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = Rule_DIGIT.parse(context);
    else if (rulename.equalsIgnoreCase("string")) rule = Rule_string.parse(context);
    else if (rulename.equalsIgnoreCase("char")) rule = Rule_char.parse(context);
    else if (rulename.equalsIgnoreCase("escape")) rule = Rule_escape.parse(context);
    else if (rulename.equalsIgnoreCase("quotation-mark")) rule = Rule_quotation_mark.parse(context);
    else if (rulename.equalsIgnoreCase("unescaped")) rule = Rule_unescaped.parse(context);
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = Rule_HEXDIG.parse(context);
    else if (rulename.equalsIgnoreCase("false")) rule = Rule_false.parse(context);
    else if (rulename.equalsIgnoreCase("null")) rule = Rule_null.parse(context);
    else if (rulename.equalsIgnoreCase("true")) rule = Rule_true.parse(context);
    else if (rulename.equalsIgnoreCase("number")) rule = Rule_number.parse(context);
    else if (rulename.equalsIgnoreCase("exp")) rule = Rule_exp.parse(context);
    else if (rulename.equalsIgnoreCase("frac")) rule = Rule_frac.parse(context);
    else if (rulename.equalsIgnoreCase("int")) rule = Rule_int.parse(context);
    else if (rulename.equalsIgnoreCase("decimal-point")) rule = Rule_decimal_point.parse(context);
    else if (rulename.equalsIgnoreCase("digit1-9")) rule = Rule_digit1_9.parse(context);
    else if (rulename.equalsIgnoreCase("e")) rule = Rule_e.parse(context);
    else if (rulename.equalsIgnoreCase("minus")) rule = Rule_minus.parse(context);
    else if (rulename.equalsIgnoreCase("plus")) rule = Rule_plus.parse(context);
    else if (rulename.equalsIgnoreCase("zero")) rule = Rule_zero.parse(context);
    else if (rulename.equalsIgnoreCase("URI")) rule = Rule_URI.parse(context);
    else if (rulename.equalsIgnoreCase("hier-part")) rule = Rule_hier_part.parse(context);
    else if (rulename.equalsIgnoreCase("scheme")) rule = Rule_scheme.parse(context);
    else if (rulename.equalsIgnoreCase("authority")) rule = Rule_authority.parse(context);
    else if (rulename.equalsIgnoreCase("userinfo")) rule = Rule_userinfo.parse(context);
    else if (rulename.equalsIgnoreCase("host")) rule = Rule_host.parse(context);
    else if (rulename.equalsIgnoreCase("port")) rule = Rule_port.parse(context);
    else if (rulename.equalsIgnoreCase("IP-literal")) rule = Rule_IP_literal.parse(context);
    else if (rulename.equalsIgnoreCase("IPvFuture")) rule = Rule_IPvFuture.parse(context);
    else if (rulename.equalsIgnoreCase("IPv6address")) rule = Rule_IPv6address.parse(context);
    else if (rulename.equalsIgnoreCase("h16")) rule = Rule_h16.parse(context);
    else if (rulename.equalsIgnoreCase("ls32")) rule = Rule_ls32.parse(context);
    else if (rulename.equalsIgnoreCase("IPv4address")) rule = Rule_IPv4address.parse(context);
    else if (rulename.equalsIgnoreCase("dec-octet")) rule = Rule_dec_octet.parse(context);
    else if (rulename.equalsIgnoreCase("reg-name")) rule = Rule_reg_name.parse(context);
    else if (rulename.equalsIgnoreCase("path-abempty")) rule = Rule_path_abempty.parse(context);
    else if (rulename.equalsIgnoreCase("path-absolute")) rule = Rule_path_absolute.parse(context);
    else if (rulename.equalsIgnoreCase("path-rootless")) rule = Rule_path_rootless.parse(context);
    else if (rulename.equalsIgnoreCase("path-empty")) rule = Rule_path_empty.parse(context);
    else if (rulename.equalsIgnoreCase("segment")) rule = Rule_segment.parse(context);
    else if (rulename.equalsIgnoreCase("segment-nz")) rule = Rule_segment_nz.parse(context);
    else if (rulename.equalsIgnoreCase("pchar")) rule = Rule_pchar.parse(context);
    else if (rulename.equalsIgnoreCase("query")) rule = Rule_query.parse(context);
    else if (rulename.equalsIgnoreCase("fragment")) rule = Rule_fragment.parse(context);
    else if (rulename.equalsIgnoreCase("pct-encoded")) rule = Rule_pct_encoded.parse(context);
    else if (rulename.equalsIgnoreCase("unreserved")) rule = Rule_unreserved.parse(context);
    else if (rulename.equalsIgnoreCase("sub-delims")) rule = Rule_sub_delims.parse(context);
    else throw new IllegalArgumentException("unknown rule");

    if (rule == null)
    {
      throw new ParserException(
        "rule \"" + (String)context.getErrorStack().peek() + "\" failed",
        context.text,
        context.getErrorIndex(),
        context.getErrorStack());
    }

    if (context.text.length() > context.index)
    {
      ParserException primaryError = 
        new ParserException(
          "extra data found",
          context.text,
          context.index,
          new Stack<String>());

      if (context.getErrorIndex() > context.index)
      {
        ParserException secondaryError = 
          new ParserException(
            "rule \"" + (String)context.getErrorStack().peek() + "\" failed",
            context.text,
            context.getErrorIndex(),
            context.getErrorStack());

        primaryError.initCause(secondaryError);
      }

      throw primaryError;
    }

    return rule;
  }

  static private Rule parse(String rulename, InputStream in, boolean trace)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (in == null)
      throw new IllegalArgumentException("null input stream");

    int ch = 0;
    StringBuffer out = new StringBuffer();
    while ((ch = in.read()) != -1)
      out.append((char)ch);

    return parse(rulename, out.toString(), trace);
  }

  static private Rule parse(String rulename, File file, boolean trace)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (file == null)
      throw new IllegalArgumentException("null file");

    BufferedReader in = new BufferedReader(new FileReader(file));
    int ch = 0;
    StringBuffer out = new StringBuffer();
    while ((ch = in.read()) != -1)
      out.append((char)ch);

    in.close();

    return parse(rulename, out.toString(), trace);
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
