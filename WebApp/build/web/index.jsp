<%-- 
    Document   : index
    Created on : Apr 30, 2016, 12:50:39 AM
    Author     : nesmayakout
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>QuineMcCluskey Solver</title>

    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/grayscale.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="http://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic" rel="stylesheet" type="text/css">
    <link href="http://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
<script>
    ${requestScope.ALERT};        
    </script>

</head>

<body id="page-top" data-spy="scroll" data-target=".navbar-fixed-top" style="background-color: white">

    <!-- Navigation -->
    <nav class="navbar navbar-custom navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-main-collapse">
                    <i class="fa fa-bars"></i>
                </button>
                <a class="navbar-brand page-scroll" href="#page-top">
                    <i class="fa"></i> Tabular Method Solver
                </a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse navbar-right navbar-main-collapse">
                <ul class="nav navbar-nav">
                    <!-- Hidden li included to remove active class from about link when scrolled up past about section -->
                    <li class="hidden">
                        <a href="#page-top"></a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#about">Steps</a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#download">Download</a>
                    </li>
                    <li>
                        <a class="page-scroll" href="#contact">Contact</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

    <!-- Intro Header -->
    <header class="intro">
        <div class="intro-body" style="background-color: #313131">
            <div class="container">
                <div class="row">
                    <div class="col-md-8 col-md-offset-2">
                    <br><br><hr>
                        <h1 class="brand-heading">Mc-Cluskey</h1>

                            <h1>Welcome to Tabular Solver!</h1>
                            <form action="/Tabular/solve" method="Get">
                            <input type="text" name="minterms" value="" placeholder="Enter minterms here" style="background-color: #313131; width: 500px; height: 50px;"><br>
                            <input type="text" name="dontcares" value="" placeholder="Enter don't cares here" style="background-color: #313131;width: 500px; height: 50px;"/> 
                            <br>


                                
                            <input type="submit" class="btn" name="submit" value="submit">
                            </form>
<!--
                            <form action="/Tabular/solve" method="post">
                            <input type="file" name="M"/>
                            <input type="file" name="D"/>
                            </form>-->
                                <form method="POST" enctype="multipart/form-data" action="/Tabular/solve">
                                    <br><b style="font-size: 1.5em">Or choose a file</b><br>
                                    <div class='btn btn-default btn-file'>Minterms File<input type="file" name="MM"><br></div>
                                    <div class='btn btn-default btn-file' name="DD">Don't Cares File<input type="file" name="DD"><br></div>
                                    <br> 
                                    <input type="submit" class="btn" value="upload" style='margin-top:  0.5em'>
                                </form>
                            
                            <br><h1>Final Result</h1>

                            <pre style="font-size: 1em" >${requestScope.FINAL}</pre>
                             <button id="a" onclick="download('myfilename.txt', 'text/plain')" class="btn btn-default">Export Results</button>
                            <br>
                               <a href="#about" class="btn page-scroll"> show steps
                            <i class="fa fa-angle-double-down animated"></i>                            
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <!-- STEPS Section -->
    <section id="about" class="container content-section text-center" style="background-color: white; color: black">
                <h2>STEPS</h2>
                    <pre>${requestScope.utilOutput}</pre>


    </section>

    <!-- Download Section -->
    <section id="download" class="content-section text-center">
        <div class="download-section">
            <div class="container">
                <div class="col-lg-8 col-lg-offset-2">
                    <h2>Download Tabular Solver</h2>
                    <p>You can download Tabular Solver for free here.</p>
                    <a href="https://github.com/yakout/Quine-McCluckey" class="btn btn-default btn-lg">Visit Download Page</a>
                </div>
            </div>
        </div>
    </section>

    <!-- Contact Section -->
    <section id="contact" class="container content-section text-center">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2" style="color: black">
                <h2>Contact Us!</h2>
                <p>Feel free to email us to provide some feedback on our Work, give us suggestions for new Features and Bug fixes, or to just say hello!</p>
                <p><a href="https://www.linkedin.com/in/yakout">Ahmed Yakout</a><br>
                        <a href="http://linkedin.com/in/marwantammam">Marwan Tammam</a><br>
                        <a href="https://www.facebook.com/m.murad.Egy/">Mohamed Murad</a><br>
                        <a href="https://www.facebook.com/fady.n.samy">Fady Nabil Samy</a><br>
                        <a href="https://www.facebook.com/mohamed.raafat.50746/">Mohamed Raafat</a><br>
                        </p>
                <ul class="list-inline banner-social-buttons">
                    <li>
                        <a href="https://github.com/yakout" class="btn btn-default btn-lg"><i class="fa fa-github fa-fw"></i> <span class="network-name">Github</span></a>
                    </li>

                </ul>
            </div>
        </div>
    </section>

    
    <!-- Footer -->
    <footer>
        <div class="container text-center">
            <p style="color: black">Copyright &copy</p>
        </div>
    </footer>

    <!-- jQuery -->
    <script src="js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>

    <!-- Plugin JavaScript -->
    <script src="js/jquery.easing.min.js"></script>

    <!-- Google Maps API Key - Use your own API key to enable the map feature. More information on the Google Maps API can be found at https://developers.google.com/maps/ -->

    <!-- Custom Theme JavaScript -->
    <script src="js/grayscale.js"></script>
    
    <script>                 
        var text = "${requestScope.SOLN}";
        function download(name, type) {
            var a = document.createElement("a");
            var file = new Blob([text], {type: type});
            window.open(URL.createObjectURL(file));
            //a.href = URL.createObjectURL(file);
            //a.download = name;
            //a.click();
        }
    </script>    

</body>

</html>
