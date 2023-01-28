package io.github.v2compose.ui.main.mine

const val TEST_HTML_TEXT = """
<div class="topic_content markdown_body"><h1>MacDown</h1>
<p><img alt="MacDown logo" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://macdown.uranusjr.com/static/images/logo-160.png"></p>
<p>Hello there! I’m <strong>MacDown</strong>, the open source Markdown editor for OS X.</p>
<p>Let me introduce myself.</p>
<h2>Markdown and I</h2>
<p><strong>Markdown</strong> is a plain text formatting syntax created by John Gruber, aiming to provide a easy-to-read and feasible markup. The original Markdown syntax specification can be found <a href="http://daringfireball.net/projects/markdown/syntax" rel="nofollow">here</a>.</p>
<p><strong>MacDown</strong> is created as a simple-to-use editor for Markdown documents. I render your Markdown contents real-time into HTML, and display them in a preview panel.</p>
<p><img alt="MacDown Screenshot" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://d.pr/i/10UGP+"></p>
<p>I support all the original Markdown syntaxes. But I can do so much more! Various popular but non-standard syntaxes can be turned on/off from the <a href="#markdown-pane" rel="nofollow"><strong>Markdown</strong> preference pane</a>.</p>
<p>You can specify extra HTML rendering options through the <a href="#rendering-pane" rel="nofollow"><strong>Rendering</strong> preference pane</a>.</p>
<p>You can customize the editor window to you liking in the <a href="#editor-pane" rel="nofollow"><strong>Editor</strong> preferences pane</a>:</p>
<p>You can configure various application (that's me!) behaviors in the <a href="#general-pane" rel="nofollow"><strong>General</strong> preference pane</a>.</p>
<h2>The Basics</h2>
<p>Before I tell you about all the extra syntaxes and capabilities I have, I'll introduce you to the basics of standard markdown. If you already know markdown, and want to jump straight to learning about the fancier things I can do, I suggest you skip to the <a href="#markdown-pane" rel="nofollow"><strong>Markdown</strong> preference pane</a>. Lets jump right in. </p>
<h3>Line Breaks</h3>
<p>To force a line break, put two spaces and a newline (return) at the end of the line.</p>
<ul>
<li>
<p>This two-line bullet
won't break</p>
</li>
<li>
<p>This two-line bullet<br>
will break</p>
</li>
</ul>
<p>Here is the code:</p>
<pre><code>* This two-line bullet 
won't break

* This two-line bullet  
will break
</code></pre>
<h3>Strong and Emphasize</h3>
<p><strong>Strong</strong>: <code>**Strong**</code> or <code>__Strong__</code> (Command-B)<br>
<em>Emphasize</em>: <code>*Emphasize*</code> or <code>_Emphasize_</code>[^emphasize] (Command-I)</p>
<h3>Headers (like this one!)</h3>
<pre><code>Header 1
========

Header 2
--------
</code></pre>
<p>or</p>
<pre><code># Header 1
## Header 2
### Header 3
#### Header 4
##### Header 5
###### Header 6
</code></pre>
<h3>Links and Email</h3>
<h4>Inline</h4>
<p>Just put angle brackets around an email and it becomes clickable: <a href="mailto:uranusjr@gmail.com">uranusjr@gmail.com</a><br>
<code>&lt;uranusjr@gmail.com&gt;</code> </p>
<p>Same thing with urls: <a href="http://macdown.uranusjr.com" rel="nofollow">http://macdown.uranusjr.com</a><br>
<code> &lt;<a href="http://macdown.uranusjr.com" rel="nofollow">http://macdown.uranusjr.com</a>&gt;</code> </p>
<p>Perhaps you want to some link text like this: <a href="http://macdown.uranusjr.com" rel="nofollow" title="Title">Macdown Website</a><br>
<code>[Macdown Website](<a href="http://macdown.uranusjr.com" rel="nofollow">http://macdown.uranusjr.com</a> "Title")</code> (The title is optional) </p>
<h4>Reference style</h4>
<p>Sometimes it looks too messy to include big long urls inline, or you want to keep all your urls together. </p>
<p>Make <a href="http://macdown.uranusjr.com" rel="nofollow" title="Title">a link</a> <code>[a link][arbitrary_id]</code> then on it's own line anywhere else in the file:<br>
<code>[arbitrary_id]: <a href="http://macdown.uranusjr.com" rel="nofollow">http://macdown.uranusjr.com</a> "Title"</code></p>
<p>If the link text itself would make a good id, you can link <a href="http://macdown.uranusjr.com" rel="nofollow">like this</a> <code>[like this][]</code>, then on it's own line anywhere else in the file:<br>
<code>[like this]: <a href="http://macdown.uranusjr.com" rel="nofollow">http://macdown.uranusjr.com</a></code> </p>
<h3>Images</h3>
<h4>Inline</h4>
<p><code>![Alt Image Text](path/or/url/to.jpg "Optional Title")</code></p>
<h4>Reference style</h4>
<p><code>![Alt Image Text][image-id]</code><br>
on it's own line elsewhere:<br>
<code>[image-id]: path/or/url/to.jpg "Optional Title"</code></p>
<h3>Lists</h3>
<ul>
<li>Lists must be preceded by a blank line (or block element)</li>
<li>Unordered lists start each item with a <code>*</code></li>
</ul>
<ul>
<li>
<code>-</code> works too<ul>
<li>
Indent a level to make a nested list<ol>
<li>Ordered lists are supported.</li>
<li>Start each item (number-period-space) like <code>1. </code></li>
<li>It doesn't matter what number you use, I will render them sequentially</li>
<li>So you might want to start each line with <code>1.</code> and let me sort it out</li>
</ol>
</li>
</ul>
</li>
</ul>
<p>Here is the code:</p>
<pre><code>* Lists must be preceded by a blank line (or block element)
* Unordered lists start each item with a `*`
- `-` works too
	* Indent a level to make a nested list
		1. Ordered lists are supported.
		2. Start each item (number-period-space) like `1. `
		42. It doesn't matter what number you use, I will render them sequentially
		1. So you might want to start each line with `1.` and let me sort it out
</code></pre>
<h3>Block Quote</h3>
<blockquote>
<p>Angle brackets <code>&gt;</code> are used for block quotes.<br>
Technically not every line needs to start with a <code>&gt;</code> as long as
there are no empty lines between paragraphs.<br>
Looks kinda ugly though.</p>
<blockquote>
<p>Block quotes can be nested. </p>
<blockquote>
<p>Multiple Levels</p>
</blockquote>
</blockquote>
<p>Most markdown syntaxes work inside block quotes.</p>
<ul>
<li>Lists</li>
<li><a href="http://macdown.uranusjr.com" rel="nofollow" title="Title">Links</a></li>
<li>Etc.</li>
</ul>
</blockquote>
<p>Here is the code:</p>
<pre><code>&gt; Angle brackets `&gt;` are used for block quotes.  
Technically not every line needs to start with a `&gt;` as long as
there are no empty lines between paragraphs.  
&gt; Looks kinda ugly though.
&gt; &gt; Block quotes can be nested.  
&gt; &gt; &gt; Multiple Levels
&gt;
&gt; Most markdown syntaxes work inside block quotes.
&gt;
&gt; * Lists
&gt; * [Links][arbitrary_id]
&gt; * Etc.
</code></pre>
<h3>Inline Code</h3>
<p><code>Inline code</code> is indicated by surrounding it with backticks:<br>
<code>`Inline code`</code></p>
<p>If your <code>code has `backticks` </code> that need to be displayed, you can use double backticks:<br>
<code>``Code with `backticks` ``</code> (mind the spaces preceding the final set of backticks)</p>
<h3>Block Code</h3>
<p>If you indent at least four spaces or one tab, I'll display a code block.</p>
<pre><code>print('This is a code block')
print('The block must be preceded by a blank line')
print('Then indent at least 4 spaces or 1 tab')
	print('Nesting does nothing. Your code is displayed Literally')
</code></pre>
<p>I also know how to do something called <a href="#fenced-code-block" rel="nofollow">Fenced Code Blocks</a> which I will tell you about later.</p>
<h3>Horizontal Rules</h3>
<p>If you type three asterisks <code>***</code> or three dashes <code>---</code> on a line, I'll display a horizontal rule:</p>
<hr>
</div>
"""

const val TEST_HTML_TEXT_2 = """
<div class="topic_content markdown_body"><h2><a></a>The Markdown Preference Pane</h2>
<p>This is where I keep all preferences related to how I parse markdown into html.<br>
<img alt="Markdown preferences pane" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://d.pr/i/RQEi+"></p>
<h3>Document Formatting</h3>
<p>The <em><strong>Smartypants</strong></em> extension automatically transforms straight quotes (<code>"</code> and <code>'</code>) in your text into typographer’s quotes (<code>“</code>, <code>”</code>, <code>‘</code>, and <code>’</code>) according to the context. Very useful if you’re a typography freak like I am. Quote and Smartypants are syntactically incompatible. If both are enabled, Quote takes precedence.</p>
<h3>Block Formatting</h3>
<h4>Table</h4>
<p>This is a table:</p>
<table>
<thead>
<tr>
<th>First Header</th>
<th>Second Header</th>
</tr>
</thead>
<tbody>
<tr>
<td>Content Cell</td>
<td>Content Cell</td>
</tr>
<tr>
<td>Content Cell</td>
<td>Content Cell</td>
</tr>
</tbody></table><p>You can align cell contents with syntax like this:</p>
<table>
<thead>
<tr>
<th>Left Aligned</th>
<th>Center Aligned</th>
<th>Right Aligned</th>
</tr>
</thead>
<tbody>
<tr>
<td>col 3 is</td>
<td>some wordy text</td>
<td>${'$'}1600</td>
</tr>
<tr>
<td>col 2 is</td>
<td>centered</td>
<td>${'$'}12</td>
</tr>
<tr>
<td>zebra stripes</td>
<td>are neat</td>
<td>${'$'}1</td>
</tr>
</tbody></table><p>The left- and right-most pipes (<code>|</code>) are only aesthetic, and can be omitted. The spaces don’t matter, either. Alignment depends solely on <code>:</code> marks.</p>
<h4><a>Fenced Code Block</a></h4>
<p>This is a fenced code block:</p>
<pre><code>print('Hello world!')
</code></pre>
<p>You can also use waves (<code>~</code>) instead of back ticks (<code>`</code>):</p>
<pre><code>print('Hello world!')
</code></pre>
<p>You can add an optional language ID at the end of the first line. The language ID will only be used to highlight the code inside if you tick the <em><strong>Enable highlighting in code blocks</strong></em> option. This is what happens if you enable it:</p>
<p><img alt="Syntax highlighting example" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://d.pr/i/9HM6+"></p>
<p>I support many popular languages as well as some generic syntax descriptions that can be used if your language of choice is not supported. See <a href="http://macdown.uranusjr.com/features/" rel="nofollow">relevant sections on the official site</a> for a full list of supported syntaxes.</p>
<h3>Inline Formatting</h3>
<p>The following is a list of optional inline markups supported:</p>
<table>
<thead>
<tr>
<th>Option name</th>
<th>Markup</th>
<th>Result if enabled</th>
</tr>
</thead>
<tbody>
<tr>
<td>Intra-word emphasis</td>
<td>So A*maz*ing</td>
<td>So A<em>maz</em>ing</td>
</tr>
<tr>
<td>Strikethrough</td>
<td>~~Much wow~~</td>
<td><del>Much wow</del></td>
</tr>
<tr>
<td>Underline [^under]</td>
<td>_So doge_</td>
<td>&lt;u&gt;So doge&lt;/u&gt;</td>
</tr>
<tr>
<td>Quote [^quote]</td>
<td>"Such editor"</td>
<td>&lt;q&gt;Such editor&lt;/q&gt;</td>
</tr>
<tr>
<td>Highlight</td>
<td>==So good==</td>
<td>&lt;mark&gt;So good&lt;/mark&gt;</td>
</tr>
<tr>
<td>Superscript</td>
<td>hoge^(fuga)</td>
<td>hoge&lt;sup&gt;fuga&lt;/sup&gt;</td>
</tr>
<tr>
<td>Autolink</td>
<td><a href="http://t.co" rel="nofollow">http://t.co</a></td>
<td><a href="http://t.co" rel="nofollow">http://t.co</a></td>
</tr>
<tr>
<td>Footnotes</td>
<td>[^4] and [^4]:</td>
<td>[^4] and footnote 4</td>
</tr>
</tbody></table><p>[^4]: You don't have to use a number. Arbitrary things like <code>[^footy note4]</code> and <code>[^footy note4]:</code> will also work. But they will <em>render</em> as numbered footnotes. Also, no need to keep your footnotes in order, I will sort out the order for you so they appear in the same order they were referenced in the text body. You can even keep some footnotes near where you referenced them, and collect others at the bottom of the file in the traditional place for footnotes. </p>
<h2><a></a>The Rendering Preference Pane</h2>
<p>This is where I keep preferences relating to how I render and style the parsed markdown in the preview window.<br>
<img alt="Rendering preferences pane" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://d.pr/i/rT4d+"></p>
<h3>CSS</h3>
<p>You can choose different css files for me to use to render your html. You can even customize or add your own custom css files.</p>
<h3>Syntax Highlighting</h3>
<p>You have already seen how I can syntax highlight your fenced code blocks. See the <a href="#fenced-code-block" rel="nofollow">Fenced Code Block</a> section if you haven’t! You can also choose different themes for syntax highlighting.</p>
<h3>TeX-like Math Syntax</h3>
<p>I can also render TeX-like math syntaxes, if you allow me to.[^math] I can do inline math like this: \( 1 + 1 \) or this (in MathML): &lt;math&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;/math&gt;, and block math:</p>
<p>\[
A^T_S = B
\]</p>
<p>or (in MathML)</p>
&lt;math display="block"&gt;
&lt;msubsup&gt;&lt;mi&gt;A&lt;/mi&gt; &lt;mi&gt;S&lt;/mi&gt; &lt;mi&gt;T&lt;/mi&gt;&lt;/msubsup&gt;
&lt;mo&gt;=&lt;/mo&gt;
&lt;mi&gt;B&lt;/mi&gt;
&lt;/math&gt;
<h3>Task List Syntax</h3>
<ol>
<li>
<input disabled="" type="checkbox"> I can render checkbox list syntax<ul>
<li><input disabled="" type="checkbox"> I support nesting</li>
<li><input disabled="" type="checkbox"> I support ordered <em>and</em> unordered lists</li>
</ul>
</li>
<li><input disabled="" type="checkbox"> I don't support clicking checkboxes directly in the html window</li>
</ol>
<h3>Jekyll front-matter</h3>
<p>If you like, I can display Jekyll front-matter in a nice table. Just make sure you put the front-matter at the very beginning of the file, and fence it with <code>---</code>. For example:</p>
<pre><code>---
title: "Macdown is my friend"
date: 2014-06-06 20:00:00
---
</code></pre>
<h3>Render newline literally</h3>
<p>Normally I require you to put two spaces and a newline (aka return) at the end of a line in order to create a line break. If you like, I can render a newline any time you end a line with a newline. However, if you enable this, markdown that looks lovely when I render it might look pretty funky when you let some <em>other</em> program render it.</p>
<h2><a></a>The General Preferences Pane</h2>
<p>This is where I keep preferences related to application behavior.<br>
<img alt="General preferences pane" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://d.pr/i/rvwu+"></p>
<p>The General Preferences Pane allows you to tell me how you want me to behave. For example, do you want me to make sure there is a document open when I launch? You can also tell me if I should constantly update the preview window as you type, or wait for you to hit <code>command-R</code> instead. Maybe you prefer your editor window on the right? Or to see the word-count as you type. This is also the place to tell me if you are interested in pre-releases of me, or just want to stick to better-tested official releases. </p>
<h2><a></a>The Editor Preference Pane</h2>
<p>This is where I keep preferences related to the behavior and styling of the editing window.<br>
<img alt="Editor preferences pane" class="embedded_image" loading="lazy" referrerpolicy="no-referrer" rel="noreferrer" src="http://d.pr/i/6OL5+"></p>
<h3>Styling</h3>
<p>My editor provides syntax highlighting. You can edit the base font and the coloring/sizing theme. I provided some default themes (courtesy of <a href="http://mouapp.com" rel="nofollow">Mou</a>’s creator, Chen Luo) if you don’t know where to start.</p>
<p>You can also edit, or even add new themes if you want to! Just click the <em><strong>Reveal</strong></em> button, and start moving things around. Remember to use the correct file extension (<code>.styles</code>), though. I’m picky about that.</p>
<p>I offer auto-completion and other functions to ease your editing experience. If you don’t like it, however, you can turn them off.</p>
<h2>Hack On</h2>
<p>That’s about it. Thanks for listening. I’ll be quiet from now on (unless there’s an update about the app—I’ll remind you for that!).</p>
<p>Happy writing!</p>
<p>[^emphasize]: If <strong>Underlines</strong> is turned on, <code>_this notation_</code> will render as underlined instead of emphasized </p>
<p>[^under]: If <strong>Underline</strong> is disabled <code>_this_</code> will be rendered as <em>emphasized</em> instead of being underlined.</p>
<p>[^quote]: <strong>Quote</strong> replaces literal <code>"</code> characters with html <code>&lt;q&gt;</code> tags. <strong>Quote</strong> and <strong>Smartypants</strong> are syntactically incompatible. If both are enabled, <strong>Quote</strong> takes precedence. Note that <strong>Quote</strong> is different from <em>blockquote</em>, which is part of standard Markdown.</p>
<p>[^math]: Internet connection required.</p>
</div>
"""


const val TEST_HTML_TEXT_3 ="""<div class="topic_content">MybatisCodeHelperPro 插件是 Intellij 下支持 Mybatis 框架的插件，实现了 Mybatis 动态 sql 支持，包括使用 Mybatis 动态标签的代码提示，代码检测，方法名生成 sql ，快速测试 sql ，表上生成 crud 等功能。插件从 2016 开始开发维护了 6 年多时间。<br><br><br>3.1.5 支持了解析动态${'$'}语句,${'$'}语句后面的 sql 可以自动提示和检测<br><br><a target="_blank" href="/i/fkvSbUCJ.gif" rel="nofollow noopener" title="在新窗口打开图片 fkvSbUCJ.gif"><img src="//i.v2ex.co/fkvSbUCJ.gif" class="embedded_image"></a><br><br><a target="_blank" href="/i/qnJk4BvU.png" rel="nofollow noopener" title="在新窗口打开图片 qnJk4BvU.png"><img src="//i.v2ex.co/qnJk4BvU.png" class="embedded_image"></a><br><br><a target="_blank" href="/i/Z8lvRsIA.png" rel="nofollow noopener" title="在新窗口打开图片 Z8lvRsIA.png"><img src="//i.v2ex.co/Z8lvRsIA.png" class="embedded_image"></a><br><br><a target="_blank" href="/i/qD1JzBKN.gif" rel="nofollow noopener" title="在新窗口打开图片 qD1JzBKN.gif"><img src="//i.v2ex.co/qD1JzBKN.gif" class="embedded_image"></a><br><br><a target="_blank" href="/i/0N1chHBO.gif" rel="nofollow noopener" title="在新窗口打开图片 0N1chHBO.gif"><img src="//i.v2ex.co/0N1chHBO.gif" class="embedded_image"></a><br><br>与其他插件的区别：<br>插件支持完整识别 mybatis 的动态标签，动态标签后面的 sql 也有自动提示和检测，包括#{},if test,bind,where,trim,include 等代码提示和检测，xml 中有更多的代码提示和检测，另外还有方法名生成 sql ，生成 testcase 等原创功能。<br><br>文档地址：<br><a target="_blank" href="https://brucege.com/doc/#/" rel="nofollow noopener">https://brucege.com/doc/#/</a><br><br>售价：<br>3 年 99 ，插件已维护多年未涨价，希望每个用 Mybatis 的都可以用得起，欢迎到 <a target="_blank" href="https://brucege.com/pay/view" rel="nofollow noopener">https://brucege.com/pay/view</a> 试用<br><br>送激活码规则<br>评论中每 10 个不同的用户 id 合起来 hash 取余送一个一年的激活码，从激活的那天开始算起，已购买插件的用户也可以参与，请勿刷评论，刷评论不送。</div>"""